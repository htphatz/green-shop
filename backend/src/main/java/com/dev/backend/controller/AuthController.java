package com.dev.backend.controller;

import com.dev.backend.dto.request.LoginReq;
import com.dev.backend.dto.request.RegisterReq;
import com.dev.backend.dto.response.APIResponse;
import com.dev.backend.dto.response.LoginRes;
import com.dev.backend.dto.response.UserRes;
import com.dev.backend.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("auth")
@RequiredArgsConstructor
public class AuthController {
    private final AuthService authService;

    @PostMapping("register")
    public APIResponse<UserRes> register(@RequestBody RegisterReq request) {
        UserRes result = authService.register(request);
        return APIResponse.<UserRes>builder().result(result).build();
    }

    @PostMapping("login")
    public APIResponse<LoginRes> login(@RequestBody LoginReq request) {
        LoginRes result = authService.login(request);
        return APIResponse.<LoginRes>builder().result(result).build();
    }
}
