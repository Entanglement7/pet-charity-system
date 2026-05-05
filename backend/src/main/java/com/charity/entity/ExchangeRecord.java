package com.charity.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("exchange_records")
public class ExchangeRecord {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long userId;
    private Long itemId;
    private Integer quantity;
    private Integer pointsCost;
    private String deliveryAddress;
    /** 0=待发货 1=已发货 2=已收货 */
    private Integer status;
    @TableLogic
    private Integer deleted;
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdAt;
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updatedAt;
}
