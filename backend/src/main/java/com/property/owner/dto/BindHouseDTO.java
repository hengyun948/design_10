package com.property.owner.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;
import java.time.LocalDate;

@Data
public class BindHouseDTO {
    @NotNull(message = "业主ID不能为空")
    private Long ownerId;
    @NotNull(message = "房屋ID不能为空")
    private Long houseId;
    private Integer bindType = 1;
    private LocalDate startDate;
    private LocalDate endDate;
}
