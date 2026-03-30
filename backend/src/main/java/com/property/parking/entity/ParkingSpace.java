package com.property.parking.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("parking_space")
public class ParkingSpace {
    @TableId(type = IdType.AUTO)
    private Long id;
    private String spaceNo;
    private String spaceType;
    private String location;
    private Integer status;
    private Long ownerId;
    private String remark;
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
    @TableLogic
    private Integer deleted;
}
