package com.charity.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("organizations")
public class Organization {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long userId;
    private String name;
    private String contact;
    private String phone;
    private String email;
    private String description;
    private String licenseUrl;
    /** 0=待审核 1=已通过 2=已拒绝 */
    private Integer status;
    private String rejectReason;
    @TableLogic
    private Integer deleted;
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdAt;
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updatedAt;
}
