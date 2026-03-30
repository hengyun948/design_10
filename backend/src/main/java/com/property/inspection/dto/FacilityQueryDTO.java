package com.property.inspection.dto;

import lombok.Data;

@Data
public class FacilityQueryDTO {
    private String keyword;
    private String facilityType;
    private Integer status;
    private Integer pageNum = 1;
    private Integer pageSize = 10;
}
