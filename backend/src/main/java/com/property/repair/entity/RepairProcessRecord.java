package com.property.repair.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("repair_process_record")
public class RepairProcessRecord {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long repairOrderId;
    private Long operatorId;
    private String actionType;
    private String actionContent;
    private Integer processStatus;
    private LocalDateTime recordTime;
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
    @TableLogic
    private Integer deleted;
}
