package com.property.parking.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@TableName("parking_record")
public class ParkingRecord {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long spaceId;
    private String carNo;
    private Long ownerId;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private BigDecimal feeAmount;
    private Integer payStatus;
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
    @TableLogic
    private Integer deleted;
}
