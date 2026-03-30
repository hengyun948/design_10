package com.property.parking.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class ParkingSpaceAddDTO {
    @NotBlank(message = "请填写车位编号")
    private String spaceNo;
    private String spaceType = "NORMAL";
    private String location;
    private Integer status = 0;
    private Long ownerId;
    private String remark;
}
