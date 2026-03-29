package com.property.owner.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@TableName("owner_house_rel")
public class OwnerHouseRel {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long ownerId;
    private Long houseId;
    private Integer bindType;
    private Integer isPrimary;
    private LocalDate startDate;
    private LocalDate endDate;
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
    @TableLogic
    private Integer deleted;
}
