package com.dev.backend.mapper;

import com.dev.backend.dto.request.PermissionReq;
import com.dev.backend.dto.response.PermissionRes;
import com.dev.backend.entity.Permission;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface PermissionMapper {
    Permission toPermission(PermissionReq request);
    PermissionRes toPermissionRes(Permission permission);
}
