package com.voting.controller;

import com.voting.entity.User;
import com.voting.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @GetMapping("/welink/login")
    public ResponseEntity<Void> welinkLogin() {
        // 实际实现中会重定向到 Welink OAuth
        String welinkAuthUrl = "https://your-welink-server/oauth2/authorize?" +
                "app_id=your-app-id&redirect_uri=http://localhost:8080/api/auth/welink/callback&response_type=code";
        return ResponseEntity.status(302).header("Location", welinkAuthUrl).build();
    }

    @GetMapping("/welink/callback")
    public ResponseEntity<Map<String, Object>> welinkCallback(@RequestParam String code) {
        // 实际实现中会根据 code 获取 access_token 和用户信息
        // 这里简化处理，假设 code 包含用户信息
        User user = authService.loginOrRegister(parseWelinkCode(code));
        Map<String, Object> response = new HashMap<>();
        response.put("user", user);
        response.put("token", "mock-jwt-token-" + user.getId());
        return ResponseEntity.ok(response);
    }

    @GetMapping("/me")
    public ResponseEntity<User> getCurrentUser(@RequestHeader("X-User-Id") Long userId) {
        User user = authService.getUserByWelinkUserId(String.valueOf(userId));
        return ResponseEntity.ok(user);
    }

    private com.voting.dto.WelinkUserInfo parseWelinkCode(String code) {
        // 实际实现中需要调用 Welink API 获取用户信息
        // 这里返回模拟数据
        com.voting.dto.WelinkUserInfo info = new com.voting.dto.WelinkUserInfo();
        info.setUserId(code);
        info.setName("User " + code);
        info.setDepartment("Department");
        info.setAvatar("https://via.placeholder.com/100");
        return info;
    }
}
