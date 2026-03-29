package com.property.owner.vo;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class OwnerHouseVO {
    private Long relId;
    private Long houseId;
    private String fullAddress;
    private String buildingNo;
    private String unitNo;
    private String roomNo;
    private BigDecimal area;
    private String houseType;
    private Integer houseStatus;
    private String houseStatusName;
    private Integer bindType;
    private String bindTypeName;
    private LocalDate startDate;
    private LocalDate endDate;
}
