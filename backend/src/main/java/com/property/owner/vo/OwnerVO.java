package com.property.owner.vo;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class OwnerVO {
    private Long id;
    private Long userId;
    private String username;
    private String realName;
    private String phone;
    private String email;
    private Integer gender;
    private String genderName;
    private String idCard;
    private String address;
    private Integer status;
    private String remark;
    private LocalDateTime createTime;
}
