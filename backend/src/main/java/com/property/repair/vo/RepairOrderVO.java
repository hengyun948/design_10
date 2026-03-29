package com.property.repair.vo;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class RepairOrderVO {
    private Long id;
    private String orderNo;
    private Long ownerId;
    private String ownerName;
    private String ownerPhone;
    private Long houseId;
    private String houseAddress;
    private String title;
    private String content;
    private String repairType;
    private String repairTypeName;
    private Integer priority;
    private String priorityName;
    private Integer status;
    private String statusName;
    private Long assignedWorkerId;
    private String workerName;
    private String workerPhone;
    private LocalDateTime acceptTime;
    private LocalDateTime assignTime;
    private LocalDateTime finishTime;
    private String imageUrls;
    private LocalDateTime createTime;
}
