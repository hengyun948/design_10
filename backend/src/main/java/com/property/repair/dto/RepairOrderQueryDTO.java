package com.property.repair.dto;

import lombok.Data;

@Data
public class RepairOrderQueryDTO {
    private String keyword;
    private Integer status;
    private String repairType;
    private Long ownerId;
    private Long assignedWorkerId;
    private Integer pageNum = 1;
    private Integer pageSize = 10;
}
