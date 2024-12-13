package com.dev.backend.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ExchangeCodeReq {
    @NotBlank(message = "Code is required")
    private String code;

    @NotBlank(message = "Client's ID is required")
    @JsonProperty("client_id")
    private String clientId;

    @NotBlank(message = "Client secret is required")
    @JsonProperty("client_secret")
    private String clientSecret;

    @NotBlank(message = "Redirect uri is required")
    @JsonProperty("redirect_uri")
    private String redirectUri;

    @NotBlank(message = "Grant type is required")
    @JsonProperty("grant_type")
    private String grantType;
}
