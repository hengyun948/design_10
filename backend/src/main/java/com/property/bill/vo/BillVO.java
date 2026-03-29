package com.property.bill.vo;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
public class BillVO {
    private Long id;
    private String billNo;
    private Long houseId;
    private String houseAddress;
    private Long ownerId;
    private String ownerName;
    private String ownerPhone;
    private String billType;
    private String billTypeName;
    private String billingPeriod;
    private BigDecimal amountDue;
    private BigDecimal amountPaid;
    private Integer status;
    private String statusName;
    private LocalDate dueDate;
    private String remark;
    private LocalDateTime createTime;
}
