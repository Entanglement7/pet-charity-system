package com.charity.security;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class LoginUser {
    private Long id;
    private String username;
    private String role;
}
