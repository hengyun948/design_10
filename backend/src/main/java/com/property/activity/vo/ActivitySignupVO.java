package com.property.activity.vo;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ActivitySignupVO {
    private Long id;
    private Long activityId;
    private String activityTitle;
    private Long ownerId;
    private String ownerName;
    private String ownerPhone;
    private LocalDateTime signupTime;
    private Integer status;
    private String statusName;
    private String remark;
}
