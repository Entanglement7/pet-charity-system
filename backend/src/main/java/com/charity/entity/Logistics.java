package com.charity.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("logistics")
public class Logistics {
    @TableId(type = IdType.AUTO)
    private Long id;
    /** donation/application/exchange */
    private String refType;
    private Long refId;
    private String company;
    private String trackingNo;
    /** 0=待发货 1=已发货 2=已收货 */
    private Integer status;
    private String senderName;
    private String senderPhone;
    private String senderAddress;
    private String receiverName;
    private String receiverPhone;
    private String receiverAddress;
    private String remark;
    private LocalDateTime shippedAt;
    private LocalDateTime receivedAt;
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdAt;
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updatedAt;
}
