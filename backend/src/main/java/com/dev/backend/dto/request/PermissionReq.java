package com.dev.backend.dto.request;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class PermissionReq {
    private String name;
}
