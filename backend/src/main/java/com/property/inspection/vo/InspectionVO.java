package com.property.inspection.vo;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class InspectionVO {
    private Long id;
    private Long facilityId;
    private String facilityName;
    private String facilityType;
    private String facilityTypeName;
    private String facilityLocation;
    private Long inspectorId;
    private String inspectorName;
    private LocalDateTime inspectionTime;
    private String result;
    private String resultName;
    private Integer status;
    private String remark;
    private LocalDateTime createTime;
}
