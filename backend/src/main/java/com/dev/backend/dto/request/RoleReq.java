package com.dev.backend.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Getter;

import java.util.Set;

@Getter
@Builder
public class RoleReq {
    @NotBlank(message = "Name is required")
    private String name;

    @NotBlank(message = "Permissions is required")
    private Set<String> permissions;
}
