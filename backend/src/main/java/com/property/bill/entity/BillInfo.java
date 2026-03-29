package com.property.bill.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@TableName("bill_info")
public class BillInfo {
    @TableId(type = IdType.AUTO)
    private Long id;
    private String billNo;
    private Long houseId;
    private Long ownerId;
    private String billType;
    private String billingPeriod;
    private BigDecimal amountDue;
    private BigDecimal amountPaid;
    private Integer status;
    private LocalDate dueDate;
    private String remark;
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
    @TableLogic
    private Integer deleted;
}
