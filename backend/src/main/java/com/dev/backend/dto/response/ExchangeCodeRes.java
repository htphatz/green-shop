package com.dev.backend.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ExchangeCodeRes {
    @JsonProperty("access_token")
    String accessToken;

    @JsonProperty("expires_in")
    Long expiresIn;

    @JsonProperty("refresh_token")
    String refreshToken;

    String scope;

    @JsonProperty("token_type")
    String tokenType;
}
