package com.dev.backend.service;

import com.dev.backend.dto.request.LoginReq;
import com.dev.backend.dto.request.RegisterReq;
import com.dev.backend.dto.response.LoginRes;
import com.dev.backend.dto.response.UserRes;
import com.dev.backend.entity.Role;
import com.dev.backend.entity.User;
import com.dev.backend.exception.AppException;
import com.dev.backend.exception.ErrorCode;
import com.dev.backend.mapper.UserMapper;
import com.dev.backend.repository.InvalidatedTokenRepository;
import com.dev.backend.repository.RoleRepository;
import com.dev.backend.repository.UserRepository;
import com.dev.backend.service.impl.AuthServiceImpl;
import com.dev.backend.service.impl.BaseRedisServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private RoleRepository roleRepository;

    @Mock
    private InvalidatedTokenRepository invalidatedTokenRepository;

    @Mock
    private BaseRedisServiceImpl<String, String, Object> baseRedisService;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private UserMapper userMapper;

    @InjectMocks
    private AuthServiceImpl authService;

    private static final String SIGNER_KEY = "1TjXchw5FloESb63Kc+DFhTARvpWL4jUGCwfGWxuG5SIf/1y/LgJxHnMqaF6A/ij";

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(authService, "signerKey", SIGNER_KEY);
        ReflectionTestUtils.setField(authService, "duration", 2);
        ReflectionTestUtils.setField(authService, "refreshDuration", 3);
    }

    @Test
    @DisplayName("Register: Successfully create new user")
    void register_Success() {
        RegisterReq req = RegisterReq.builder()
                .firstName("John")
                .lastName("Doe")
                .email("john@example.com")
                .password("password123")
                .build();

        Role userRole = Role.builder().name(Role.USER).build();
        when(passwordEncoder.encode(req.getPassword())).thenReturn("encodedPassword");
        when(roleRepository.findById(Role.USER)).thenReturn(Optional.of(userRole));
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

        UserRes expectedRes = UserRes.builder()
                .id("user-123")
                .email("john@example.com")
                .firstName("John")
                .lastName("Doe")
                .build();
        when(userMapper.toUserRes(any(User.class))).thenReturn(expectedRes);

        UserRes res = authService.register(req);

        assertThat(res).isNotNull();
        assertThat(res.getEmail()).isEqualTo("john@example.com");
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    @DisplayName("Register: Throws USER_EXISTED when email already exists")
    void register_UserExisted_ThrowsException() {
        RegisterReq req = RegisterReq.builder()
                .firstName("John")
                .lastName("Doe")
                .email("existing@example.com")
                .password("password123")
                .build();

        when(passwordEncoder.encode(any())).thenReturn("encodedPassword");
        when(roleRepository.findById(Role.USER)).thenReturn(Optional.of(new Role()));
        when(userRepository.save(any(User.class))).thenThrow(new DataIntegrityViolationException("Duplicate entry"));

        AppException ex = assertThrows(AppException.class, () -> authService.register(req));
        assertThat(ex.getErrorCode()).isEqualTo(ErrorCode.USER_EXISTED);
    }

    @Test
    @DisplayName("Login: Throws USER_NOT_FOUND when user does not exist")
    void login_UserNotFound_ThrowsException() {
        LoginReq req = LoginReq.builder()
                .email("nonexistent@example.com")
                .password("password123")
                .build();

        when(userRepository.findByEmail(req.getEmail())).thenReturn(Optional.empty());

        AppException ex = assertThrows(AppException.class, () -> authService.login(req));
        assertThat(ex.getErrorCode()).isEqualTo(ErrorCode.USER_NOT_FOUND);
    }

    @Test
    @DisplayName("Login: Throws PASSWORD_INVALID when password does not match")
    void login_InvalidPassword_ThrowsException() {
        LoginReq req = LoginReq.builder()
                .email("john@example.com")
                .password("wrongpassword")
                .build();

        User user = User.builder()
                .id("user-123")
                .email("john@example.com")
                .password("encodedPassword")
                .build();

        when(userRepository.findByEmail(req.getEmail())).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("wrongpassword", "encodedPassword")).thenReturn(false);

        AppException ex = assertThrows(AppException.class, () -> authService.login(req));
        assertThat(ex.getErrorCode()).isEqualTo(ErrorCode.PASSWORD_INVALID);
    }

    @Test
    @DisplayName("Login: Successfully generate JWT access & refresh token")
    void login_Success() throws Exception {
        LoginReq req = LoginReq.builder()
                .email("john@example.com")
                .password("password123")
                .build();

        Role role = Role.builder().name(Role.USER).build();
        User user = User.builder()
                .id("user-123")
                .email("john@example.com")
                .password("encodedPassword")
                .roles(Set.of(role))
                .build();

        when(userRepository.findByEmail(req.getEmail())).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("password123", "encodedPassword")).thenReturn(true);
        when(userRepository.save(any(User.class))).thenReturn(user);

        LoginRes res = authService.login(req);

        assertThat(res).isNotNull();
        assertThat(res.getAccessToken()).isNotBlank();
        verify(userRepository, times(1)).save(user);
    }
}
