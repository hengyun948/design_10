package com.property.notice.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class NoticeAddDTO {
    @NotBlank(message = "请填写公告标题")
    private String title;
    @NotBlank(message = "请填写公告内容")
    private String content;
    private String noticeType;
}
