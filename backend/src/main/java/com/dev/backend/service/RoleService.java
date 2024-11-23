package com.dev.backend.service;

import com.dev.backend.dto.request.RoleReq;
import com.dev.backend.dto.response.RoleRes;

import java.util.List;

public interface RoleService {
    RoleRes createRole(RoleReq request);
    List<RoleRes> getAllRoles();
    void deleteRole(String roleName);
}
