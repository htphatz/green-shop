package com.dev.backend.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class IntrospectReq {
    @NotBlank(message = "Token is required")
    private String token;
}
