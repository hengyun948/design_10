package com.property.activity.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ActivityAddDTO {
    @NotBlank(message = "请填写活动标题")
    private String title;
    private String content;
    private String activityType = "ACTIVITY";
    private LocalDateTime activityTime;
    private String location;
    private Integer maxParticipants;
}
