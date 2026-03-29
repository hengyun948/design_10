package com.property.house.dto;

import lombok.Data;

@Data
public class HouseQueryDTO {
    private String buildingNo;
    private String unitNo;
    private String roomNo;
    private Integer houseStatus;
    private Integer pageNum = 1;
    private Integer pageSize = 10;
}
