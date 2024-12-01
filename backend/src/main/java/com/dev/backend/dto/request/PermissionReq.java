package com.dev.backend.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class PermissionReq {
    @NotBlank(message = "Name is required")
    private String name;
}
