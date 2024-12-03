package com.dev.backend.service.impl;

import com.dev.backend.dto.request.IntrospectReq;
import com.dev.backend.dto.request.LoginReq;
import com.dev.backend.dto.request.LogoutReq;
import com.dev.backend.dto.request.RegisterReq;
import com.dev.backend.dto.response.IntrospectRes;
import com.dev.backend.dto.response.LoginRes;
import com.dev.backend.dto.response.UserRes;
import com.dev.backend.entity.InvalidatedToken;
import com.dev.backend.entity.Role;
import com.dev.backend.entity.User;
import com.dev.backend.exception.AppException;
import com.dev.backend.exception.ErrorCode;
import com.dev.backend.mapper.UserMapper;
import com.dev.backend.repository.InvalidatedTokenRepository;
import com.dev.backend.repository.RoleRepository;
import com.dev.backend.repository.UserRepository;
import com.dev.backend.service.AuthService;
import com.nimbusds.jose.*;
import com.nimbusds.jose.crypto.MACSigner;
import com.nimbusds.jose.crypto.MACVerifier;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.text.ParseException;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthServiceImpl implements AuthService {
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final InvalidatedTokenRepository invalidatedTokenRepository;
    private final BaseRedisServiceImpl<String, String, Object> baseRedisService;
    private final PasswordEncoder passwordEncoder;
    private final UserMapper userMapper;

    private static final String FAILED_LOGIN_PREFIX = "failed_login";
    private static final String BLOCKED_PREFIX = "blocked";
    private static final int MAX_FAILED_LOGIN = 10;
    private static final long LOGIN_TIMEOUT_MINUTES = 2;
    private static final long LOCK_TIME_MINUTES = 10;

    @Value(value = "${jwt.signerKey}")
    String signerKey;

    @Value(value = "${jwt.duration}")
    int duration;

    @Override
    public UserRes register(RegisterReq request) {
        User user = User.builder()
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .build();

        Set<Role> roles = new HashSet<>();
        roleRepository.findById(Role.USER).ifPresent(roles::add);
        user.setRoles(roles);

        try {
            userRepository.save(user);
        } catch (DataIntegrityViolationException exception) {
            throw new AppException(ErrorCode.USER_EXISTED);
        }
        return userMapper.toUserRes(user);
    }

    @Override
    public LoginRes login(LoginReq request) throws KeyLengthException {
        User existingUser = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));
        if (!passwordEncoder.matches(request.getPassword(), existingUser.getPassword())) {
            throw new AppException(ErrorCode.PASSWORD_INVALID);
        }
        String token = generateToken(existingUser);
        return LoginRes.builder().token(token).build();
    }

    @Override
    public LoginRes loginWithRedis(LoginReq request) throws KeyLengthException {
        String fieldKey = FAILED_LOGIN_PREFIX;
        int failedLoginQuantity;
        User existingUser = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));

        if (baseRedisService.hashExists(existingUser.getId(), BLOCKED_PREFIX)) {
            throw new AppException(ErrorCode.ACCOUNT_BLOCKED);
        }
        if (!passwordEncoder.matches(request.getPassword(), existingUser.getPassword())) {
            if (baseRedisService.hashExists(existingUser.getId(), fieldKey)) {
                failedLoginQuantity = (int) baseRedisService.hashGet(existingUser.getId(), fieldKey) + 1;
            } else {
                failedLoginQuantity = 1;
            }
            baseRedisService.hashSet(existingUser.getId(), fieldKey, failedLoginQuantity);
            if (failedLoginQuantity >= MAX_FAILED_LOGIN) {
                baseRedisService.setTimeToLive(existingUser.getId(), LOCK_TIME_MINUTES);
                baseRedisService.hashSet(existingUser.getId(), BLOCKED_PREFIX, true);
                throw new AppException(ErrorCode.ACCOUNT_BLOCKED);
            }
            throw new AppException(ErrorCode.PASSWORD_INVALID);
        }
        // Đăng nhập thành công
        baseRedisService.delete(existingUser.getId(), fieldKey);
        baseRedisService.delete(existingUser.getId(), "locked");
        baseRedisService.setTimeToLive(existingUser.getId(), LOGIN_TIMEOUT_MINUTES);
        String token = generateToken(existingUser);
        return LoginRes.builder().token(token).build();
    }

    @Override
    public IntrospectRes introspect(IntrospectReq request) throws JOSEException, ParseException {
        String token = request.getToken();
        boolean isValid = true;
        try {
            verifyToken(token);
        } catch (AppException e) {
            isValid = false;
        }
        return IntrospectRes.builder().isValid(isValid).build();
    }

    @Override
    public void logout(LogoutReq request) throws ParseException, JOSEException {
        try {
            SignedJWT signedJWT = verifyToken(request.getToken());
            String jit = signedJWT.getJWTClaimsSet().getJWTID();
            Date expirationTime = signedJWT.getJWTClaimsSet().getExpirationTime();

            InvalidatedToken invalidatedToken = InvalidatedToken.builder()
                    .id(jit)
                    .expirationTime(expirationTime)
                    .build();
            invalidatedTokenRepository.save(invalidatedToken);
        } catch (AppException exception) {
            log.info("Token already expired");
        }
    }

    private String generateToken(User user) throws KeyLengthException {
        JWSHeader header = new JWSHeader(JWSAlgorithm.HS512);

        JWTClaimsSet claimsSet = new JWTClaimsSet.Builder()
                .subject(user.getEmail())
                .jwtID(UUID.randomUUID().toString())
                .issueTime(new Date())
                .expirationTime(new Date(
                        Instant.now().plus(duration, ChronoUnit.HOURS).toEpochMilli()
                ))
                // Spring tự động phân quyền với JWT thông qua claim "SCOPE"
                .claim("scope", buildScope(user))
                .build();

        Payload payload = new Payload(claimsSet.toJSONObject());

        JWSObject jwsObject = new JWSObject(header, payload);

        try {
            jwsObject.sign(new MACSigner(signerKey.getBytes()));
            return jwsObject.serialize();
        } catch (JOSEException e) {
            log.error("Cannot create JWT");
            throw new RuntimeException(e);
        }
    }

    private String buildScope(User user) {
        StringJoiner stringJoiner = new StringJoiner(" ");
        if (!CollectionUtils.isEmpty(user.getRoles())) {
            user.getRoles().forEach(role -> {
                stringJoiner.add("ROLE_" + role.getName());
                if (!CollectionUtils.isEmpty(role.getPermissions())) {
                    role.getPermissions().forEach(permission -> {
                        stringJoiner.add(permission.getName());
                    });
                }
            });
        }
        return stringJoiner.toString();
    }

    private SignedJWT verifyToken(String token) throws JOSEException, ParseException {
        JWSVerifier verifier = new MACVerifier(signerKey.getBytes());

        SignedJWT signedJWT = SignedJWT.parse(token);

        Date expirationDate = signedJWT.getJWTClaimsSet().getExpirationTime();

        boolean verified = signedJWT.verify(verifier);

        // Kiếm tra thời hạn và chữ ký
        if (!(verified && expirationDate.after(new Date()))) {
            throw new AppException(ErrorCode.UNAUTHORIZED);
        }

        // Kiểm tra đã logout chưa (logout tức là token đã nằm trong bảng invalidated_tokens)
        if (invalidatedTokenRepository.existsById(signedJWT.getJWTClaimsSet().getJWTID())) {
            throw new AppException(ErrorCode.UNAUTHORIZED);
        }

        return signedJWT;
    }
}
