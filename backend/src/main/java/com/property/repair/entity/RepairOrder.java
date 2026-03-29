package com.property.repair.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("repair_order")
public class RepairOrder {
    @TableId(type = IdType.AUTO)
    private Long id;
    private String orderNo;
    private Long ownerId;
    private Long houseId;
    private String title;
    private String content;
    private String repairType;
    private Integer priority;
    private Integer status;
    private Long assignedWorkerId;
    private LocalDateTime acceptTime;
    private LocalDateTime assignTime;
    private LocalDateTime finishTime;
    private String imageUrls;
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
    @TableLogic
    private Integer deleted;
}
