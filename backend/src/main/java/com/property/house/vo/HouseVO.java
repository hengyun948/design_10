package com.property.house.vo;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class HouseVO {
    private Long id;
    private String buildingNo;
    private String unitNo;
    private String roomNo;
    private String fullAddress;
    private BigDecimal area;
    private String houseType;
    private Integer houseStatus;
    private String houseStatusName;
    private String ownerName;
    private String ownerPhone;
    private String remark;
    private LocalDateTime createTime;
}
