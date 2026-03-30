package com.property.activity.dto;

import lombok.Data;

@Data
public class ActivityQueryDTO {
    private String keyword;
    private String activityType;
    private Integer status;
    private Integer pageNum = 1;
    private Integer pageSize = 10;
}
