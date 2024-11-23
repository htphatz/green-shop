package com.dev.backend.service;

import com.dev.backend.dto.request.PermissionReq;
import com.dev.backend.dto.response.PermissionRes;

import java.util.List;

public interface PermissionService {
    PermissionRes createPermission(PermissionReq request);
    List<PermissionRes> getAllPermissions();
    void deletePermission(String permissionName);
}
