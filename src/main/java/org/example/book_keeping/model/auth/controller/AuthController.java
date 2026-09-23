package org.example.book_keeping.model.auth.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.book_keeping.common.result.Result;
import org.example.book_keeping.config.security.LoginUser;
import org.example.book_keeping.config.security.SecurityUtils;
import org.example.book_keeping.model.auth.dto.AuthTokenResponse;
import org.example.book_keeping.model.auth.dto.LoginRequest;
import org.example.book_keeping.model.auth.dto.RegisterRequest;
import org.example.book_keeping.model.auth.service.AuthService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 鉴权接口。实际路径前缀含 context-path：/api/auth/**
 */
@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/register")
    public Result<AuthTokenResponse> register(@Valid @RequestBody RegisterRequest request) {
        return Result.success(authService.register(request));
    }

    @PostMapping("/login")
    public Result<AuthTokenResponse> login(@Valid @RequestBody LoginRequest request) {
        return Result.success(authService.login(request));
    }

    /**
     * 获取当前登录用户信息（需 Token）。
     */
    @GetMapping("/me")
    public Result<LoginUser> me() {
        return Result.success(SecurityUtils.getLoginUser());
    }
}
