package com.property.owner.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("owner_info")
public class OwnerInfo {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long userId;
    private Integer gender;
    private String idCard;
    private String address;
    private String remark;
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
    @TableLogic
    private Integer deleted;
}
