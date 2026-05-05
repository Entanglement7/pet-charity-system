package com.charity.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("points_records")
public class PointsRecord {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long userId;
    private Integer amount;
    /** donate/exchange */
    private String type;
    private Long refId;
    private String description;
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdAt;
}
