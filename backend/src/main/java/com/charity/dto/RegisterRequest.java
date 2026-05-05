package com.charity.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class RegisterRequest {
    @NotBlank(message = "用户名不能为空")
    @Size(min = 2, max = 32, message = "用户名长度在2到32个字符")
    private String username;

    @NotBlank(message = "密码不能为空")
    @Size(min = 6, message = "密码不能少于6位")
    private String password;

    @NotBlank(message = "请选择角色")
    @Pattern(regexp = "user|organization", message = "角色只能为 user 或 organization")
    private String role;
}
