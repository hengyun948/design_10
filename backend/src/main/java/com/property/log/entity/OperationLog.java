package com.property.log.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("operation_log")
public class OperationLog {
    @TableId(type = IdType.AUTO)
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
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
}
