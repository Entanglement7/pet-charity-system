package com.charity.dto;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class ApplicationRecordVO {
    private Long id;
    private Long itemId;
    private String itemTitle;
    private String itemCategory;
    private Integer quantity;
    private String reason;
    private Integer status;
    private String rejectReason;
    private String usageReport;
    private Integer usageStatus;
    private String usageRejectReason;
    private LocalDateTime createdAt;
}
