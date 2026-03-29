package com.property.system.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.property.common.model.PageResult;
import com.property.system.dto.UserAddDTO;
import com.property.system.dto.UserQueryDTO;
import com.property.system.dto.UserUpdateDTO;
import com.property.system.entity.SysUser;
import com.property.system.vo.UserVO;

public interface SysUserService extends IService<SysUser> {
    PageResult<UserVO> pageUsers(UserQueryDTO dto);
    void addUser(UserAddDTO dto);
    void updateUser(Long id, UserUpdateDTO dto);
    void deleteUser(Long id);
    void updatePassword(Long userId, String oldPassword, String newPassword);
    UserVO getUserById(Long id);
}
