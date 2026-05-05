package com.charity.dto;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class PublicityRecordVO {
    private Long id;
    private String donorName;
    private String title;
    private String category;
    private Integer quantity;
    private String unit;
    private Integer status;
    private Integer pointsAwarded;
    private LocalDateTime createdAt;
}
