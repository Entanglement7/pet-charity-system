package com.charity.dto;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class FlowVO {
    private Long id;
    private String refType;
    private Long refId;
    private String itemName;
    private String donorName;
    private String receiverName;
    private String receiverAddress;
    private String company;
    private String trackingNo;
    private Integer status;
    private LocalDateTime shippedAt;
    private LocalDateTime receivedAt;
    private String usageReport;
    private LocalDateTime createdAt;
}
