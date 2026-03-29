package com.property.house.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import java.math.BigDecimal;

@Data
public class HouseAddDTO {
    @NotBlank(message = "楼栋号不能为空")
    private String buildingNo;
    @NotBlank(message = "单元号不能为空")
    private String unitNo;
    @NotBlank(message = "房间号不能为空")
    private String roomNo;
    private BigDecimal area;
    private String houseType;
    private Integer houseStatus = 0;
    private String remark;
}
