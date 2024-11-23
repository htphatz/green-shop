package com.dev.backend.service;

import com.dev.backend.dto.request.LoginReq;
import com.dev.backend.dto.request.RegisterReq;
import com.dev.backend.dto.response.LoginRes;
import com.dev.backend.dto.response.UserRes;

public interface AuthService {
    UserRes register(RegisterReq request);
    LoginRes login(LoginReq request);
}
