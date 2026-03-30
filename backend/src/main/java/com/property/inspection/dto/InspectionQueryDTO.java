package com.property.inspection.dto;

import lombok.Data;

@Data
public class InspectionQueryDTO {
    private Long facilityId;
    private Long inspectorId;
    private String result;
    private Integer pageNum = 1;
    private Integer pageSize = 10;
}
