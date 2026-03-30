package com.property.inspection.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("facility_info")
public class FacilityInfo {
    @TableId(type = IdType.AUTO)
    private Long id;
    private String facilityName;
    private String facilityType;
    private String location;
    private Integer status;
    private LocalDateTime lastMaintenanceTime;
    private String remark;
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
    @TableLogic
    private Integer deleted;
}
