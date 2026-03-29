package com.property.bill.dto;

import lombok.Data;

@Data
public class BillQueryDTO {
    private Long houseId;
    private Long ownerId;
    private String billType;
    private String billingPeriod;
    private Integer status;
    private Integer pageNum = 1;
    private Integer pageSize = 10;
}
