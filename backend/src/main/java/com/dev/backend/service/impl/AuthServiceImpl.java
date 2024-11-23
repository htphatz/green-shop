package com.dev.backend.service.impl;

import com.dev.backend.dto.request.LoginReq;
import com.dev.backend.dto.request.RegisterReq;
import com.dev.backend.dto.response.LoginRes;
import com.dev.backend.dto.response.UserRes;
import com.dev.backend.entity.Role;
import com.dev.backend.entity.User;
import com.dev.backend.exception.AppException;
import com.dev.backend.exception.ErrorCode;
import com.dev.backend.mapper.UserMapper;
import com.dev.backend.repository.RoleRepository;
import com.dev.backend.repository.UserRepository;
import com.dev.backend.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {
    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final RoleRepository roleRepository;

    @Override
    public UserRes register(RegisterReq request) {
        User user = User.builder()
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .email(request.getEmail())
                .password(request.getPassword())
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
    public LoginRes login(LoginReq request) {
        User existingUser = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));
        if (!existingUser.getPassword().equals(request.getPassword())) {
            throw new AppException(ErrorCode.PASSWORD_INVALID);
        }
        return LoginRes.builder().token("fake token").build();
    }
}
