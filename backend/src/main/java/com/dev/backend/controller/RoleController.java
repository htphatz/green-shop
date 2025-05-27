package com.dev.backend.controller;

import com.dev.backend.dto.request.RoleReq;
import com.dev.backend.dto.response.APIResponse;
import com.dev.backend.dto.response.RoleRes;
import com.dev.backend.service.RoleService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("roles")
@RequiredArgsConstructor
@Tag(name = "Role APIs")
public class RoleController {
    private final RoleService roleService;

    @PostMapping
    @Operation(summary = "Create role")
    public APIResponse<RoleRes> createRole(@Valid @RequestBody RoleReq request) {
        RoleRes result = roleService.createRole(request);
        return APIResponse.<RoleRes>builder().result(result).build();
    }

    @GetMapping
    @Operation(summary = "Get all roles")
    public APIResponse<List<RoleRes>> getAllRoles() {
        List<RoleRes> result = roleService.getAllRoles();
        return APIResponse.<List<RoleRes>>builder().result(result).build();
    }

    @DeleteMapping("{roleName}")
    @Operation(summary = "Delete role")
    public APIResponse<Void> deleteRole(@PathVariable("roleName") String roleName) {
        roleService.deleteRole(roleName);
        return APIResponse.<Void>builder().build();
    }
}
