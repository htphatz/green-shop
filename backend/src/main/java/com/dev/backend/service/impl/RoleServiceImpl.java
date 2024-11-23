package com.dev.backend.service.impl;

import com.dev.backend.dto.request.RoleReq;
import com.dev.backend.dto.response.RoleRes;
import com.dev.backend.entity.Permission;
import com.dev.backend.entity.Role;
import com.dev.backend.mapper.RoleMapper;
import com.dev.backend.repository.PermissionRepository;
import com.dev.backend.repository.RoleRepository;
import com.dev.backend.service.RoleService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RoleServiceImpl implements RoleService {
    private final RoleRepository roleRepository;
    private final PermissionRepository permissionRepository;
    private final RoleMapper roleMapper;

    @Override
    public RoleRes createRole(RoleReq request) {
        Role role = roleMapper.toRole(request);
        List<Permission> permissions = permissionRepository.findAllById(request.getPermissions());
        role.setPermissions(new HashSet<>(permissions));
        return roleMapper.toRoleRes(roleRepository.save(role));
    }

    @Override
    public List<RoleRes> getAllRoles() {
        List<Role> roles = roleRepository.findAll();
        return roles.stream().map(roleMapper::toRoleRes).collect(Collectors.toList());
    }

    @Override
    public void deleteRole(String roleName) {
        roleRepository.deleteById(roleName);
    }
}
