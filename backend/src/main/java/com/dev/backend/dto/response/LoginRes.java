package com.dev.backend.dto.response;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class LoginRes {
    private String token;
}
