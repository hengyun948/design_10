package com.property.repair.vo;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class RepairProcessVO {
    private Long id;
    private String actionType;
    private String actionTypeName;
    private String actionContent;
    private Integer processStatus;
    private String statusName;
    private Long operatorId;
    private String operatorName;
    private LocalDateTime recordTime;
}
