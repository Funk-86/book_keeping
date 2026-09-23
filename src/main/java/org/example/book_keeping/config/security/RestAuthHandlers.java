package org.example.book_keeping.config.security;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.example.book_keeping.common.result.ResultCode;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

/**
 * 未认证 / 无权限时返回统一 Result JSON，而不是默认 HTML/空 body。
 */
@Component
@RequiredArgsConstructor
public class RestAuthHandlers implements AuthenticationEntryPoint, AccessDeniedHandler {

    private final SecurityJsonWriter securityJsonWriter;

    @Override
    public void commence(HttpServletRequest request,
                         HttpServletResponse response,
                         AuthenticationException authException) throws IOException {
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        securityJsonWriter.write(response, ResultCode.UNAUTHORIZED);
    }

    @Override
    public void handle(HttpServletRequest request,
                       HttpServletResponse response,
                       AccessDeniedException accessDeniedException) throws IOException {
        response.setStatus(HttpServletResponse.SC_FORBIDDEN);
        securityJsonWriter.write(response, ResultCode.FORBIDDEN);
    }
}
