package com.charity.dto;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class ExchangeRecordVO {
    private Long id;
    private Long itemId;
    private String itemName;
    private Integer quantity;
    private Integer pointsCost;
    private String deliveryAddress;
    private Integer status;
    private LocalDateTime createdAt;
}
