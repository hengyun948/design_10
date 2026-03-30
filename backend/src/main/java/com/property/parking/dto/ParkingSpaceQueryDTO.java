package com.property.parking.dto;

import lombok.Data;

@Data
public class ParkingSpaceQueryDTO {
    private String keyword;
    private String spaceType;
    private Integer status;
    private Integer pageNum = 1;
    private Integer pageSize = 10;
}
