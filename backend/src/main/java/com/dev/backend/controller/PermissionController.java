package com.dev.backend.controller;

import com.dev.backend.dto.request.PermissionReq;
import com.dev.backend.dto.response.APIResponse;
import com.dev.backend.dto.response.PermissionRes;
import com.dev.backend.service.PermissionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("permissions")
@RequiredArgsConstructor
@Tag(name = "Permission APIs")
public class PermissionController {
    private final PermissionService permissionService;

    @PostMapping
    @Operation(summary = "Create permission")
    public APIResponse<PermissionRes> createPermission(@Valid @RequestBody PermissionReq request) {
        PermissionRes result = permissionService.createPermission(request);
        return APIResponse.<PermissionRes>builder().result(result).build();
    }

    @GetMapping
    @Operation(summary = "Get all permission")
    public APIResponse<List<PermissionRes>> getAllPermissions() {
        List<PermissionRes> result = permissionService.getAllPermissions();
        return APIResponse.<List<PermissionRes>>builder().result(result).build();
    }

    @DeleteMapping("{permissionName}")
    @Operation(summary = "Delete permission")
    public APIResponse<Void> deletePermission(@PathVariable("permissionName") String permissionName) {
        permissionService.deletePermission(permissionName);
        return APIResponse.<Void>builder().build();
    }
}
