package com.property.parking.vo;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ParkingSpaceVO {
    private Long id;
    private String spaceNo;
    private String spaceType;
    private String spaceTypeName;
    private String location;
    private Integer status;
    private String statusName;
    private Long ownerId;
    private String ownerName;
    private String remark;
    private LocalDateTime createTime;
}
