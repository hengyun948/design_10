package com.property.system.vo;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class UserVO {
    private Long id;
    private String username;
    private String realName;
    private String phone;
    private String email;
    private String roleCode;
    private String roleName;
    private Integer status;
    private String avatar;
    private LocalDateTime lastLoginTime;
    private LocalDateTime createTime;
}
