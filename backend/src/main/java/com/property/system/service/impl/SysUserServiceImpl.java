package com.property.system.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.property.common.model.PageResult;
import com.property.exception.BusinessException;
import com.property.system.dto.UserAddDTO;
import com.property.system.dto.UserQueryDTO;
import com.property.system.dto.UserUpdateDTO;
import com.property.system.entity.SysUser;
import com.property.system.mapper.SysUserMapper;
import com.property.system.service.SysUserService;
import com.property.system.vo.UserVO;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.Map;

@Service
@RequiredArgsConstructor
public class SysUserServiceImpl extends ServiceImpl<SysUserMapper, SysUser> implements SysUserService {

    private final PasswordEncoder passwordEncoder;

    private static final Map<String, String> ROLE_NAME_MAP = Map.of(
            "ADMIN", "系统管理员",
            "PROPERTY_STAFF", "物业人员",
            "REPAIR_WORKER", "维修人员",
            "OWNER", "业主"
    );

    @Override
    public PageResult<UserVO> pageUsers(UserQueryDTO dto) {
        Page<SysUser> page = new Page<>(dto.getPageNum(), dto.getPageSize());
        LambdaQueryWrapper<SysUser> wrapper = new LambdaQueryWrapper<SysUser>()
                .like(StringUtils.hasText(dto.getUsername()), SysUser::getUsername, dto.getUsername())
                .like(StringUtils.hasText(dto.getRealName()), SysUser::getRealName, dto.getRealName())
                .eq(StringUtils.hasText(dto.getRoleCode()), SysUser::getRoleCode, dto.getRoleCode())
                .eq(dto.getStatus() != null, SysUser::getStatus, dto.getStatus())
                .orderByDesc(SysUser::getCreateTime);
        Page<SysUser> result = baseMapper.selectPage(page, wrapper);
        return PageResult.of(result.convert(this::toVO));
    }

    @Override
    public void addUser(UserAddDTO dto) {
        long count = count(new LambdaQueryWrapper<SysUser>().eq(SysUser::getUsername, dto.getUsername()));
        if (count > 0) {
            throw new BusinessException("用户名已存在");
        }
        SysUser user = new SysUser();
        user.setUsername(dto.getUsername());
        user.setPassword(passwordEncoder.encode(dto.getPassword()));
        user.setRealName(dto.getRealName());
        user.setPhone(dto.getPhone());
        user.setEmail(dto.getEmail());
        user.setRoleCode(dto.getRoleCode());
        user.setStatus(1);
        save(user);
    }

    @Override
    public void updateUser(Long id, UserUpdateDTO dto) {
        SysUser user = getById(id);
        if (user == null) throw new BusinessException("用户不存在");
        if (StringUtils.hasText(dto.getRealName())) user.setRealName(dto.getRealName());
        if (StringUtils.hasText(dto.getPhone())) user.setPhone(dto.getPhone());
        if (StringUtils.hasText(dto.getEmail())) user.setEmail(dto.getEmail());
        if (StringUtils.hasText(dto.getRoleCode())) user.setRoleCode(dto.getRoleCode());
        if (dto.getStatus() != null) user.setStatus(dto.getStatus());
        if (StringUtils.hasText(dto.getAvatar())) user.setAvatar(dto.getAvatar());
        updateById(user);
    }

    @Override
    public void deleteUser(Long id) {
        removeById(id);
    }

    @Override
    public void updatePassword(Long userId, String oldPassword, String newPassword) {
        SysUser user = getById(userId);
        if (user == null) throw new BusinessException("用户不存在");
        if (!passwordEncoder.matches(oldPassword, user.getPassword())) {
            throw new BusinessException("原密码错误");
        }
        user.setPassword(passwordEncoder.encode(newPassword));
        updateById(user);
    }

    @Override
    public UserVO getUserById(Long id) {
        SysUser user = getById(id);
        if (user == null) throw new BusinessException("用户不存在");
        return toVO(user);
    }

    private UserVO toVO(SysUser user) {
        UserVO vo = new UserVO();
        vo.setId(user.getId());
        vo.setUsername(user.getUsername());
        vo.setRealName(user.getRealName());
        vo.setPhone(user.getPhone());
        vo.setEmail(user.getEmail());
        vo.setRoleCode(user.getRoleCode());
        vo.setRoleName(ROLE_NAME_MAP.getOrDefault(user.getRoleCode(), user.getRoleCode()));
        vo.setStatus(user.getStatus());
        vo.setAvatar(user.getAvatar());
        vo.setLastLoginTime(user.getLastLoginTime());
        vo.setCreateTime(user.getCreateTime());
        return vo;
    }
}
