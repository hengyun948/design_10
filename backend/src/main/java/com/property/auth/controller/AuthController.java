package com.property.auth.controller;

import com.property.auth.dto.LoginDTO;
import com.property.auth.service.AuthService;
import com.property.auth.vo.LoginVO;
import com.property.common.api.Result;
import jakarta.validation.Valid;
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
}
