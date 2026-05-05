package com.charity.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("exchange_items")
public class ExchangeItem {
    @TableId(type = IdType.AUTO)
    private Long id;
    private String name;
    private String description;
    private String image;
    private Integer pointsCost;
    private Integer stock;
    @TableLogic
    private Integer deleted;
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdAt;
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updatedAt;
}
