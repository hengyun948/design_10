package com.property.parking.dto;

import lombok.Data;

@Data
public class ParkingRecordQueryDTO {
    private String carNo;
    private Long spaceId;
    private Long ownerId;
    private Integer payStatus;
    private Integer pageNum = 1;
    private Integer pageSize = 10;
}
