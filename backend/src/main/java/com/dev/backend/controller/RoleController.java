package com.dev.backend.controller;

import com.dev.backend.dto.request.RoleReq;
import com.dev.backend.dto.response.APIResponse;
import com.dev.backend.dto.response.RoleRes;
import com.dev.backend.service.RoleService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("roles")
@RequiredArgsConstructor
public class RoleController {
    private final RoleService roleService;

    @PostMapping
    public APIResponse<RoleRes> createRole(@Valid @RequestBody RoleReq request) {
        RoleRes result = roleService.createRole(request);
        return APIResponse.<RoleRes>builder().result(result).build();
    }

    @GetMapping
    public APIResponse<List<RoleRes>> getAllRoles() {
        List<RoleRes> result = roleService.getAllRoles();
        return APIResponse.<List<RoleRes>>builder().result(result).build();
    }

    @DeleteMapping("{roleName}")
    public APIResponse<Void> deleteRole(@PathVariable("roleName") String roleName) {
        roleService.deleteRole(roleName);
        return APIResponse.<Void>builder().build();
    }
}
