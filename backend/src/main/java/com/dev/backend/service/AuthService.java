package com.dev.backend.service;

import com.dev.backend.dto.request.IntrospectReq;
import com.dev.backend.dto.request.LoginReq;
import com.dev.backend.dto.request.LogoutReq;
import com.dev.backend.dto.request.RegisterReq;
import com.dev.backend.dto.response.IntrospectRes;
import com.dev.backend.dto.response.LoginRes;
import com.dev.backend.dto.response.UserRes;
import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.KeyLengthException;

import java.text.ParseException;

public interface AuthService {
    UserRes register(RegisterReq request);
    LoginRes login(LoginReq request) throws KeyLengthException;
    LoginRes loginWithRedis(LoginReq request) throws KeyLengthException;
    IntrospectRes introspect(IntrospectReq request) throws JOSEException, ParseException;
    void logout(LogoutReq request) throws ParseException, JOSEException;
}
