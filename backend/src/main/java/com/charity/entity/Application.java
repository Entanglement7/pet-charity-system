package com.charity.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("applications")
public class Application {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long organizationId;
    private Long itemId;
    private Integer quantity;
    private String reason;
    /** 0=待审核 1=审核通过 2=审核拒绝 3=已发货 4=已收货 */
    private Integer status;
    private String rejectReason;
    private String usageReport;
    /** 0=未填写 1=待审核 2=已公示 3=已驳回 */
    private Integer usageStatus;
    private String usageRejectReason;
    @TableLogic
    private Integer deleted;
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdAt;
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updatedAt;
}
