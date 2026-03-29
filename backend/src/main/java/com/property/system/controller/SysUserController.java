package com.property.system.controller;

import com.property.common.api.Result;
import com.property.common.model.PageResult;
import com.property.system.dto.UserAddDTO;
import com.property.system.dto.UserQueryDTO;
import com.property.system.dto.UserUpdateDTO;
import com.property.system.service.SysUserService;
import com.property.system.vo.UserVO;
import com.property.utils.SecurityUtils;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class SysUserController {

    private final SysUserService userService;

    @GetMapping
    @PreAuthorize("hasAuthority('ADMIN')")
    public Result<PageResult<UserVO>> pageUsers(UserQueryDTO dto) {
        return Result.success(userService.pageUsers(dto));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('ADMIN')")
    public Result<UserVO> getUser(@PathVariable Long id) {
        return Result.success(userService.getUserById(id));
    }

    @PostMapping
    @PreAuthorize("hasAuthority('ADMIN')")
    public Result<?> addUser(@Valid @RequestBody UserAddDTO dto) {
        userService.addUser(dto);
        return Result.success();
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('ADMIN')")
    public Result<?> updateUser(@PathVariable Long id, @RequestBody UserUpdateDTO dto) {
        userService.updateUser(id, dto);
        return Result.success();
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('ADMIN')")
    public Result<?> deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
        return Result.success();
    }

    @PutMapping("/password")
    public Result<?> updatePassword(@RequestParam String oldPassword, @RequestParam String newPassword) {
        Long userId = SecurityUtils.getCurrentUserId();
        userService.updatePassword(userId, oldPassword, newPassword);
        return Result.success();
    }
}
