package com.property.complaint.dto;

import lombok.Data;

@Data
public class ComplaintQueryDTO {
    private String keyword;
    private String category;
    private Integer status;
    private Long ownerId;
    private Integer pageNum = 1;
    private Integer pageSize = 10;
}
