package com.property.activity.vo;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ActivityVO {
    private Long id;
    private String title;
    private String content;
    private String activityType;
    private String activityTypeName;
    private LocalDateTime activityTime;
    private String location;
    private Integer maxParticipants;
    private Integer signupCount;
    private Integer status;
    private String statusName;
    private Long publisherId;
    private String publisherName;
    private LocalDateTime createTime;
    private Boolean signedUp;
}
