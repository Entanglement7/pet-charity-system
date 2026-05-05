package com.charity.controller;

import com.charity.common.R;
import com.charity.dto.LoginRequest;
import com.charity.dto.RegisterRequest;
import com.charity.entity.User;
import com.charity.mapper.UserMapper;
import com.charity.security.JwtTokenProvider;
import com.charity.security.LoginUser;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@Tag(name = "用户认证")
@RestController
@RequestMapping("/vue-element-admin/user")
@RequiredArgsConstructor
public class UserController {

    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;

    @Operation(summary = "登录")
    @PostMapping("/login")
    public R<Map<String, String>> login(@Valid @RequestBody LoginRequest req) {
        User user = userMapper.findByUsername(req.getUsername());
        if (user == null || !passwordEncoder.matches(req.getPassword(), user.getPassword())) {
            return R.fail(60204, "用户名或密码错误");
        }
        String token = jwtTokenProvider.generateToken(user.getId(), user.getUsername(), user.getRole());
        return R.ok(Map.of("token", token));
    }

    @Operation(summary = "获取当前用户信息")
    @GetMapping("/info")
    public R<Map<String, Object>> getInfo(@AuthenticationPrincipal LoginUser loginUser) {
        User user = userMapper.selectById(loginUser.getId());
        return R.ok(Map.of(
                "roles", new String[]{user.getRole()},
                "name", user.getUsername(),
                "avatar", user.getAvatar() != null ? user.getAvatar() : "",
                "introduction", user.getIntroduction() != null ? user.getIntroduction() : ""
        ));
    }

    @Operation(summary = "登出")
    @PostMapping("/logout")
    public R<String> logout() {
        return R.ok("success");
    }

    @Operation(summary = "注册")
    @PostMapping("/register")
    public R<Void> register(@Valid @RequestBody RegisterRequest req) {
        if (userMapper.findByUsername(req.getUsername()) != null) {
            return R.fail("用户名已存在");
        }
        User user = new User();
        user.setUsername(req.getUsername());
        user.setPassword(passwordEncoder.encode(req.getPassword()));
        user.setRole(req.getRole());
        user.setAvatar("/default-avatar.png");
        userMapper.insert(user);
        return R.ok();
    }
}
