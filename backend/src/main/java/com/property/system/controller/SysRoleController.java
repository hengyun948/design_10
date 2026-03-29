package com.property.system.controller;

import com.property.common.api.Result;
import com.property.system.entity.SysRole;
import com.property.system.service.SysRoleService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/roles")
@RequiredArgsConstructor
public class SysRoleController {

    private final SysRoleService roleService;

    @GetMapping
    public Result<List<SysRole>> listRoles() {
        return Result.success(roleService.listAllRoles());
    }
}
