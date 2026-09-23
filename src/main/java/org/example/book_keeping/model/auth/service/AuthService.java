package org.example.book_keeping.model.auth.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import lombok.RequiredArgsConstructor;
import org.example.book_keeping.common.exception.BusinessException;
import org.example.book_keeping.common.result.ResultCode;
import org.example.book_keeping.config.security.JwtTokenProvider;
import org.example.book_keeping.model.auth.dto.AuthTokenResponse;
import org.example.book_keeping.model.auth.dto.LoginRequest;
import org.example.book_keeping.model.auth.dto.RegisterRequest;
import org.example.book_keeping.model.auth.entity.User;
import org.example.book_keeping.model.auth.mapper.UserMapper;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 注册 / 登录业务。
 */
@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;

    /**
     * 注册并直接返回 Token（免再调登录）。
     */
    @Transactional(rollbackFor = Exception.class)
    public AuthTokenResponse register(RegisterRequest request) {
        Long count = userMapper.selectCount(new LambdaQueryWrapper<User>()
                .eq(User::getUsername, request.getUsername()));
        if (count != null && count > 0) {
            throw new BusinessException(ResultCode.USER_ALREADY_EXISTS);
        }
        User user = new User();
        user.setUsername(request.getUsername());
        user.setPasswordHash(passwordEncoder.encode(request.getPassword()));
        userMapper.insert(user);
        return buildTokenResponse(user);
    }

    /**
     * 登录校验密码并签发 JWT。
     */
    public AuthTokenResponse login(LoginRequest request) {
        User user = userMapper.selectOne(new LambdaQueryWrapper<User>()
                .eq(User::getUsername, request.getUsername())
                .last("LIMIT 1"));
        if (user == null) {
            throw new BusinessException(ResultCode.BAD_CREDENTIALS);
        }
        if (!passwordEncoder.matches(request.getPassword(), user.getPasswordHash())) {
            throw new BusinessException(ResultCode.BAD_CREDENTIALS);
        }
        return buildTokenResponse(user);
    }

    private AuthTokenResponse buildTokenResponse(User user) {
        String token = jwtTokenProvider.createToken(user.getId(), user.getUsername());
        return AuthTokenResponse.builder()
                .token(token)
                .tokenType("Bearer")
                .expiresIn(jwtTokenProvider.getExpireSeconds())
                .userId(user.getId())
                .username(user.getUsername())
                .build();
    }
}
