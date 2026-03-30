package com.property.notice.dto;

import lombok.Data;

@Data
public class NoticeQueryDTO {
    private String keyword;
    private String noticeType;
    private Integer publishStatus;
    private Integer pageNum = 1;
    private Integer pageSize = 10;
}
