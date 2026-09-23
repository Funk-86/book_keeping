package org.example.book_keeping.model.auth.service;

import org.example.book_keeping.model.auth.dto.AuthTokenResponse;
import org.example.book_keeping.model.auth.dto.LoginRequest;
import org.example.book_keeping.model.auth.dto.RegisterRequest;

public interface AuthService {

    /**
     * 注册并返回 Token。
     */
    AuthTokenResponse register(RegisterRequest request);

    /**
     * 登录并返回 Token。
     */
    AuthTokenResponse login(LoginRequest request);
}
