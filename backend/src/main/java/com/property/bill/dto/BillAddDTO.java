package com.property.bill.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class BillAddDTO {
    @NotNull(message = "房屋ID不能为空")
    private Long houseId;
    @NotNull(message = "业主ID不能为空")
    private Long ownerId;
    @NotBlank(message = "费用类型不能为空")
    private String billType;
    @NotBlank(message = "账期不能为空")
    private String billingPeriod;
    @NotNull(message = "应缴金额不能为空")
    private BigDecimal amountDue;
    private LocalDate dueDate;
    private String remark;
}
