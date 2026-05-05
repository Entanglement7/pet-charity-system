package com.charity.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("organization_addresses")
public class OrganizationAddress {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long organizationId;
    private String receiver;
    private String phone;
    private String province;
    private String city;
    private String district;
    private String detail;
    private Integer isDefault;
    @TableLogic
    private Integer deleted;
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdAt;
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updatedAt;
}
