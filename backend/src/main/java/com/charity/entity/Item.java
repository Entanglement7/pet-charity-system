package com.charity.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("items")
public class Item {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long donationId;
    private String title;
    private String category;
    private Integer quantity;
    private Integer remaining;
    private String unit;
    private String description;
    private String images;
    @TableLogic
    private Integer deleted;
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdAt;
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updatedAt;
}
