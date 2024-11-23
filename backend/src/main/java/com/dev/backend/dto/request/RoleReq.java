package com.dev.backend.dto.request;

import lombok.Builder;
import lombok.Getter;

import java.util.Set;

@Getter
@Builder
public class RoleReq {
    private String name;
    private Set<String> permissions;
}
