package com.dev.backend.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ExchangeCodeReq {
    String code;

    @JsonProperty("client_id")
    String clientId;

    @JsonProperty("client_secret")
    String clientSecret;

    @JsonProperty("redirect_uri")
    String redirectUri;

    @JsonProperty("grant_type")
    String grantType;
}
