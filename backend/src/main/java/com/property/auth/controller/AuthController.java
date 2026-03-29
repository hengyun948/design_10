package com.property.auth.controller;

import com.property.auth.dto.LoginDTO;
import com.property.auth.service.AuthService;
import com.property.auth.vo.LoginVO;
import com.property.common.api.Result;
import jakarta.validation.Valid;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/login")
    public Result<LoginVO> login(@Valid @RequestBody LoginDTO dto) {
        return Result.success(authService.login(dto));
    }

    @GetMapping("/me")
    public Result<LoginVO> getCurrentUser() {
        return Result.success(authService.getCurrentUser());
    }

    @PostMapping("/logout")
    public Result<?> logout() {
        return Result.success();
    }

    @PostMapping("/change-password")
    public Result<Void> changePassword(@RequestBody ChangePasswordRequest req) {
        authService.changePassword(req.getOldPassword(), req.getNewPassword());
        return Result.success();
    }

    @PutMapping("/profile")
    public Result<Void> updateProfile(@RequestBody UpdateProfileRequest req) {
        authService.updateProfile(req.getRealName(), req.getPhone(), req.getEmail());
        return Result.success();
    }

    @Data
    static class ChangePasswordRequest {
        private String oldPassword;
        private String newPassword;
    }

    @Data
    static class UpdateProfileRequest {
        private String realName;
        private String phone;
        private String email;
    }
}
