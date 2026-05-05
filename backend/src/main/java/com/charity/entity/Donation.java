package com.charity.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("donations")
public class Donation {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long userId;
    private String title;
    private String category;
    private String conditionLevel;
    private Integer quantity;
    private String unit;
    private String description;
    private String images;
    /** 0=待审核 1=审核通过 2=审核拒绝 3=已发货 4=已收货 */
    private Integer status;
    private String rejectReason;
    private Integer pointsAwarded;
    @TableLogic
    private Integer deleted;
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdAt;
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updatedAt;
}
