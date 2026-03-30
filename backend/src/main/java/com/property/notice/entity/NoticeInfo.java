package com.property.notice.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("notice_info")
public class NoticeInfo {
    @TableId(type = IdType.AUTO)
    private Long id;
    private String title;
    private String content;
    private String noticeType;
    private Integer publishStatus;
    private Long publisherId;
    private LocalDateTime publishTime;
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
    @TableLogic
    private Integer deleted;
}
