package com.property.repair.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class RepairAssignDTO {
    @NotNull(message = "请选择维修人员")
    private Long workerId;
    private String remark;
}
