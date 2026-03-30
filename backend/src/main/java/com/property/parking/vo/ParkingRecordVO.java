package com.property.parking.vo;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class ParkingRecordVO {
    private Long id;
    private Long spaceId;
    private String spaceNo;
    private String spaceLocation;
    private String carNo;
    private Long ownerId;
    private String ownerName;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private BigDecimal feeAmount;
    private Integer payStatus;
    private String payStatusName;
    private LocalDateTime createTime;
}
