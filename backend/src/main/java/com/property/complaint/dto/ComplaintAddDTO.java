package com.property.complaint.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class ComplaintAddDTO {
    @NotBlank(message = "请填写标题")
    private String title;
    @NotBlank(message = "请填写内容")
    private String content;
    private String category = "COMPLAINT";
}
