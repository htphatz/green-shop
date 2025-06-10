package com.dev.backend.controller;

import com.dev.backend.dto.request.*;
import com.dev.backend.dto.response.*;
import com.dev.backend.service.AuthService;
import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.KeyLengthException;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.text.ParseException;

@RestController
@RequestMapping("auth")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Auth APIs")
public class AuthController {
    private final AuthService authService;

    @PostMapping("register")
    @Operation(summary = "Register")
    public APIResponse<UserRes> register(@Valid @RequestBody RegisterReq request) {
        UserRes result = authService.register(request);
        return APIResponse.<UserRes>builder().result(result).build();
    }

    @PostMapping("login")
    @Operation(summary = "Login")
    public APIResponse<LoginRes> login(@Valid @RequestBody LoginReq request) throws KeyLengthException {
        LoginRes result = authService.loginWithRedis(request);
        return APIResponse.<LoginRes>builder().result(result).build();
    }

    @PostMapping("introspect")
    public APIResponse<IntrospectRes> introspect(@Valid @RequestBody IntrospectReq request) throws ParseException, JOSEException {
        IntrospectRes result = authService.introspect(request);
        return APIResponse.<IntrospectRes>builder().result(result).build();
    }

    @PostMapping("refresh")
    @Operation(summary = "Refresh token")
    public APIResponse<RefreshTokenRes> refresh(@Valid @RequestBody RefreshTokenReq request) {
        RefreshTokenRes result = authService.refreshToken(request);
        return APIResponse.<RefreshTokenRes>builder().result(result).build();
    }

    @PostMapping("logout")
    @Operation(summary = "Logout")
    public APIResponse<Void> logout(@Valid @RequestBody LogoutReq request) throws ParseException, JOSEException {
        authService.logout(request);
        return APIResponse.<Void>builder().build();
    }

    @PostMapping("reactivate/{userId}")
    @Operation(summary = "Reactivate user account")
    public APIResponse<Void> reactivateUser(@PathVariable String userId) {
        authService.reactivateUser(userId);
        return APIResponse.<Void>builder()
                .message("User account has been reactivated successfully")
                .build();
    }

    @PostMapping("admin/clear-failed-attempts/{userId}")
    @Operation(summary = "Clear failed login attempts (Admin only)")
    public APIResponse<Void> clearFailedLoginAttempts(@PathVariable String userId) {
        authService.clearFailedLoginAttempts(userId);
        return APIResponse.<Void>builder()
                .message("Failed login attempts cleared successfully")
                .build();
    }
}
