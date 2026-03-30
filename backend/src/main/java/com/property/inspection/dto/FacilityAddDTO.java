package com.property.inspection.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class FacilityAddDTO {
    @NotBlank(message = "请填写设施名称")
    private String facilityName;
    private String facilityType;
    private String location;
    private Integer status = 1;
    private LocalDateTime lastMaintenanceTime;
    private String remark;
}
