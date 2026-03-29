package com.property.owner.dto;

import lombok.Data;

@Data
public class OwnerQueryDTO {
    private String keyword;
    private Integer pageNum = 1;
    private Integer pageSize = 10;
}
