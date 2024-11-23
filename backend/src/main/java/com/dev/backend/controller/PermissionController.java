package com.dev.backend.controller;

import com.dev.backend.dto.request.PermissionReq;
import com.dev.backend.dto.response.APIResponse;
import com.dev.backend.dto.response.PermissionRes;
import com.dev.backend.service.PermissionService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("permissions")
@RequiredArgsConstructor
public class PermissionController {
    private final PermissionService permissionService;

    @PostMapping
    public APIResponse<PermissionRes> createPermission(@RequestBody PermissionReq request) {
        PermissionRes result = permissionService.createPermission(request);
        return APIResponse.<PermissionRes>builder().result(result).build();
    }

    @GetMapping
    public APIResponse<List<PermissionRes>> getAllPermissions() {
        List<PermissionRes> result = permissionService.getAllPermissions();
        return APIResponse.<List<PermissionRes>>builder().result(result).build();
    }

    @DeleteMapping("{permissionName}")
    public APIResponse<Void> deletePermission(@PathVariable("permissionName") String permissionName) {
        permissionService.deletePermission(permissionName);
        return APIResponse.<Void>builder().build();
    }
}
