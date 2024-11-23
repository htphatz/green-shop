package com.dev.backend.service.impl;

import com.dev.backend.dto.request.PermissionReq;
import com.dev.backend.dto.response.PermissionRes;
import com.dev.backend.entity.Permission;
import com.dev.backend.mapper.PermissionMapper;
import com.dev.backend.repository.PermissionRepository;
import com.dev.backend.service.PermissionService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PermissionServiceImpl implements PermissionService {
    private final PermissionRepository permissionRepository;
    private final PermissionMapper permissionMapper;

    @Override
    public PermissionRes createPermission(PermissionReq request) {
        Permission permission = permissionMapper.toPermission(request);
        return permissionMapper.toPermissionRes(permissionRepository.save(permission));
    }

    @Override
    public List<PermissionRes> getAllPermissions() {
        List<Permission> permissions = permissionRepository.findAll();
        return permissions.stream().map(permissionMapper::toPermissionRes).collect(Collectors.toList());
    }

    @Override
    public void deletePermission(String permissionName) {
        permissionRepository.deleteById(permissionName);
    }
}
