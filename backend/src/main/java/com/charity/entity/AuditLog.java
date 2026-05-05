package com.charity.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("audit_logs")
public class AuditLog {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long auditorId;
    /** organization/donation/application */
    private String refType;
    private Long refId;
    /** approve/reject */
    private String action;
    private String reason;
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdAt;
}
