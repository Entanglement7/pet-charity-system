package com.charity.dto;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class UserPointsVO {
    private Long id;
    private Long userId;
    private String username;
    private Integer balance;
    private Integer totalEarned;
    private Integer totalSpent;
    private LocalDateTime updatedAt;
}
