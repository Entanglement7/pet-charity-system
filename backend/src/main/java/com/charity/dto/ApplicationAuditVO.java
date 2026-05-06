package com.charity.dto;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class ApplicationAuditVO {
    private Long id;
    private Long organizationId;
    private String organizationName;
    private Long itemId;
    private String itemTitle;
    private Integer quantity;
    private String reason;
    private Integer status;
    private String usageReport;
    private Integer usageStatus;
    private String usageRejectReason;
    private LocalDateTime createdAt;
}
