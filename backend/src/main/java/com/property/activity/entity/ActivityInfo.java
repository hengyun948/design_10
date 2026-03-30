package com.property.activity.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("activity_info")
public class ActivityInfo {
    @TableId(type = IdType.AUTO)
    private Long id;
    private String title;
    private String content;
    private String activityType;
    private LocalDateTime activityTime;
    private String location;
    private Integer maxParticipants;
    private Integer status;
    private Long publisherId;
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
    @TableLogic
    private Integer deleted;
}
