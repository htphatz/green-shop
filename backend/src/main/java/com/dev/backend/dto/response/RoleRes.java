package com.dev.backend.dto.response;

import com.dev.backend.entity.Permission;
import lombok.Builder;
import lombok.Getter;

import java.util.Set;

@Getter
@Builder
public class RoleRes {
    private String name;
    private Set<Permission> permissions;
}
