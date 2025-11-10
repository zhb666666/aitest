package com.trading.controller;

import com.trading.common.Result;
import com.trading.entity.Permission;
import com.trading.service.PermissionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/permissions")
public class PermissionController {

    @Autowired
    private PermissionService permissionService;

    @GetMapping
    @PreAuthorize("hasAuthority('permission:list')")
    public Result<List<Permission>> getAllPermissions() {
        return Result.success(permissionService.getAllPermissions());
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('permission:view')")
    public Result<Permission> getPermissionById(@PathVariable Long id) {
        return permissionService.getPermissionById(id)
                .map(Result::success)
                .orElse(Result.error("权限不存在"));
    }

    @PostMapping
    @PreAuthorize("hasAuthority('permission:add')")
    public Result<Permission> createPermission(@RequestBody Permission permission) {
        return Result.success(permissionService.createPermission(permission));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('permission:edit')")
    public Result<Permission> updatePermission(@PathVariable Long id, @RequestBody Permission permission) {
        permission.setId(id);
        return Result.success(permissionService.updatePermission(permission));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('permission:delete')")
    public Result<Void> deletePermission(@PathVariable Long id) {
        permissionService.deletePermission(id);
        return Result.success(null);
    }
}

