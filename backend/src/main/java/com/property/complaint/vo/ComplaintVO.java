package com.property.complaint.vo;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ComplaintVO {
    private Long id;
    private Long ownerId;
    private String ownerName;
    private String ownerPhone;
    private String title;
    private String content;
    private String category;
    private String categoryName;
    private Integer status;
    private String statusName;
    private String replyContent;
    private Long replyBy;
    private String replyByName;
    private LocalDateTime replyTime;
    private LocalDateTime createTime;
}
