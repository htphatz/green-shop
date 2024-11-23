package com.dev.backend.mapper;

import com.dev.backend.dto.request.RoleReq;
import com.dev.backend.dto.response.RoleRes;
import com.dev.backend.entity.Role;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface RoleMapper {
    @Mapping(target = "permissions", ignore = true)
    Role toRole(RoleReq request);

    RoleRes toRoleRes(Role role);
}
