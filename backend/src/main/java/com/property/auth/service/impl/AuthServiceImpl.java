package com.property.auth.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.property.auth.dto.LoginDTO;
import com.property.auth.service.AuthService;
import com.property.auth.vo.LoginVO;
import com.property.exception.BusinessException;
import com.property.security.utils.JwtUtils;
import com.property.system.entity.SysUser;
import com.property.system.mapper.SysUserMapper;
import com.property.utils.SecurityUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final SysUserMapper userMapper;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtils jwtUtils;

    private static final Map<String, String> ROLE_NAME_MAP = Map.of(
            "ADMIN", "系统管理员",
            "PROPERTY_STAFF", "物业人员",
            "REPAIR_WORKER", "维修人员",
            "OWNER", "业主"
    );

    @Override
    public LoginVO login(LoginDTO dto) {
        SysUser user = userMapper.selectOne(
                new LambdaQueryWrapper<SysUser>().eq(SysUser::getUsername, dto.getUsername())
        );
        if (user == null || !passwordEncoder.matches(dto.getPassword(), user.getPassword())) {
            throw new BusinessException(401, "用户名或密码错误");
        }
        if (user.getStatus() != 1) {
            throw new BusinessException(403, "账号已被禁用");
        }
        user.setLastLoginTime(LocalDateTime.now());
        userMapper.updateById(user);

        String token = jwtUtils.generateToken(user.getId(), user.getUsername(), user.getRoleCode());
        return buildLoginVO(user, token);
    }

    @Override
    public LoginVO getCurrentUser() {
        Long userId = SecurityUtils.getCurrentUserId();
        if (userId == null) throw new BusinessException(401, "未登录");
        SysUser user = userMapper.selectById(userId);
        if (user == null) throw new BusinessException("用户不存在");
        return buildLoginVO(user, null);
    }

    @Override
    public void changePassword(String oldPassword, String newPassword) {
        Long userId = SecurityUtils.getCurrentUserId();
        SysUser user = userMapper.selectById(userId);
        if (user == null) throw new BusinessException("用户不存在");
        if (!passwordEncoder.matches(oldPassword, user.getPassword())) {
            throw new BusinessException("原密码错误");
        }
        user.setPassword(passwordEncoder.encode(newPassword));
        userMapper.updateById(user);
    }

    private LoginVO buildLoginVO(SysUser user, String token) {
        LoginVO vo = new LoginVO();
        vo.setToken(token);
        vo.setUserId(user.getId());
        vo.setUsername(user.getUsername());
        vo.setRealName(user.getRealName());
        vo.setRoleCode(user.getRoleCode());
        vo.setRoleName(ROLE_NAME_MAP.getOrDefault(user.getRoleCode(), user.getRoleCode()));
        vo.setAvatar(user.getAvatar());
        return vo;
    }
}
