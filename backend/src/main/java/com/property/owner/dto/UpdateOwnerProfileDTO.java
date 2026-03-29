package com.property.owner.dto;

import lombok.Data;

@Data
public class UpdateOwnerProfileDTO {
    private String realName;
    private String phone;
    private String email;
    private Integer gender;
    private String idCard;
    private String address;
    private String remark;
}
