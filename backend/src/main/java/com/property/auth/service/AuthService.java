package com.property.auth.service;

import com.property.auth.dto.LoginDTO;
import com.property.auth.vo.LoginVO;

public interface AuthService {
    LoginVO login(LoginDTO dto);
    LoginVO getCurrentUser();
    void changePassword(String oldPassword, String newPassword);
    void updateProfile(String realName, String phone, String email);
}
