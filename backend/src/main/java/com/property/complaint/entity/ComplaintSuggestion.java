package com.property.complaint.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("complaint_suggestion")
public class ComplaintSuggestion {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long ownerId;
    private String title;
    private String content;
    private String category;
    private Integer status;
    private String replyContent;
    private Long replyBy;
    private LocalDateTime replyTime;
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
    @TableLogic
    private Integer deleted;
}
