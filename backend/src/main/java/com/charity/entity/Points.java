package com.charity.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("points")
public class Points {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long userId;
    private Integer balance;
    private Integer totalEarned;
    private Integer totalSpent;
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdAt;
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updatedAt;
}
