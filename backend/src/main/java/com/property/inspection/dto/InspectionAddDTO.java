package com.property.inspection.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class InspectionAddDTO {
    @NotNull(message = "请选择巡检设施")
    private Long facilityId;
    @NotNull(message = "请填写巡检时间")
    private LocalDateTime inspectionTime;
    private String result = "NORMAL";
    private String remark;
}
