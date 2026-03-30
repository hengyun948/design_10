package com.property.parking.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class ParkingRecordAddDTO {
    @NotNull(message = "请选择车位")
    private Long spaceId;
    @NotBlank(message = "请填写车牌号")
    private String carNo;
    private Long ownerId;
    @NotNull(message = "请填写进入时间")
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private BigDecimal feeAmount;
    private Integer payStatus = 0;
}
