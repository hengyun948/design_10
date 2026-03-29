package com.property.system.dto;

import lombok.Data;

@Data
public class UserUpdateDTO {
    private String realName;
    private String phone;
    private String email;
    private String roleCode;
    private Integer status;
    private String avatar;
}
