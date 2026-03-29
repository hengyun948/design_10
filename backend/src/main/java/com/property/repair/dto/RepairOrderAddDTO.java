package com.property.repair.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class RepairOrderAddDTO {
    @NotNull(message = "请选择房屋")
    private Long houseId;
    @NotBlank(message = "请填写报修标题")
    private String title;
    @NotBlank(message = "请描述问题")
    private String content;
    private String repairType;
    private Integer priority = 2;
    private String imageUrls;
}
