package com.property.parking.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class PayParkingRecordDTO {
    @NotNull(message = "请填写离开时间")
    private LocalDateTime endTime;
    @NotNull(message = "请填写停车费用")
    private BigDecimal feeAmount;
}
