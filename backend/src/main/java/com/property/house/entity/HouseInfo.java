package com.property.house.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@TableName("house_info")
public class HouseInfo {
    @TableId(type = IdType.AUTO)
    private Long id;
    private String buildingNo;
    private String unitNo;
    private String roomNo;
    private BigDecimal area;
    private String houseType;
    private Integer houseStatus;
    private String remark;
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
    @TableLogic
    private Integer deleted;
}
