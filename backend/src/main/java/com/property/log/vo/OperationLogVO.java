package com.property.log.vo;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class OperationLogVO {
    private Long id;
    private String moduleName;
    private String operationType;
    private String requestUri;
    private String requestMethod;
    private Long operatorId;
    private String operatorName;
    private String operationDesc;
    private String ip;
    private Long executeTime;
    private Integer status;
    private String errorMsg;
    private LocalDateTime createTime;
}
