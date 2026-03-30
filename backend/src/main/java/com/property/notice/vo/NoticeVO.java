package com.property.notice.vo;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class NoticeVO {
    private Long id;
    private String title;
    private String content;
    private String noticeType;
    private String noticeTypeName;
    private Integer publishStatus;
    private String publishStatusName;
    private Long publisherId;
    private String publisherName;
    private LocalDateTime publishTime;
    private LocalDateTime createTime;
}
