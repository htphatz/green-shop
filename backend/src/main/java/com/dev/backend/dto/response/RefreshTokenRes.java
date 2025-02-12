package com.dev.backend.dto.response;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class RefreshTokenRes {
    private String userId;
    private String accessToken;
}
