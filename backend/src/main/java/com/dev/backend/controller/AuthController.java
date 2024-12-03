package com.dev.backend.controller;

import com.dev.backend.dto.request.IntrospectReq;
import com.dev.backend.dto.request.LoginReq;
import com.dev.backend.dto.request.LogoutReq;
import com.dev.backend.dto.request.RegisterReq;
import com.dev.backend.dto.response.APIResponse;
import com.dev.backend.dto.response.IntrospectRes;
import com.dev.backend.dto.response.LoginRes;
import com.dev.backend.dto.response.UserRes;
import com.dev.backend.service.AuthService;
import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.KeyLengthException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.text.ParseException;

@RestController
@RequestMapping("auth")
@RequiredArgsConstructor
public class AuthController {
    private final AuthService authService;

    @PostMapping("register")
    public APIResponse<UserRes> register(@Valid @RequestBody RegisterReq request) {
        UserRes result = authService.register(request);
        return APIResponse.<UserRes>builder().result(result).build();
    }

    @PostMapping("login")
    public APIResponse<LoginRes> login(@Valid @RequestBody LoginReq request) throws KeyLengthException {
        LoginRes result = authService.login(request);
        return APIResponse.<LoginRes>builder().result(result).build();
    }

    @PostMapping("loginWithRedis")
    public APIResponse<LoginRes> loginWithRedis(@Valid @RequestBody LoginReq request) throws KeyLengthException {
        LoginRes result = authService.loginWithRedis(request);
        return APIResponse.<LoginRes>builder().result(result).build();
    }

    @PostMapping("introspect")
    public APIResponse<IntrospectRes> introspect(@Valid @RequestBody IntrospectReq request) throws ParseException, JOSEException {
        IntrospectRes result = authService.introspect(request);
        return APIResponse.<IntrospectRes>builder().result(result).build();
    }

    @PostMapping("logout")
    public APIResponse<Void> logout(@Valid @RequestBody LogoutReq request) throws ParseException, JOSEException {
        authService.logout(request);
        return APIResponse.<Void>builder().build();
    }
}
