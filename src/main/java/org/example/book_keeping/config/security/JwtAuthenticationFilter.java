package org.example.book_keeping.config.security;

import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.example.book_keeping.common.exception.BusinessException;
import org.example.book_keeping.common.result.ResultCode;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

/**
 * 从 Authorization 头解析 JWT 并写入 SecurityContext。
 */
@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtProperties jwtProperties;
    private final JwtTokenProvider jwtTokenProvider;
    private final SecurityJsonWriter securityJsonWriter;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        String token = resolveToken(request);
        if (!StringUtils.hasText(token)) {
            filterChain.doFilter(request, response);
            return;
        }
        try {
            Claims claims = jwtTokenProvider.parseClaims(token);
            Long userId = jwtTokenProvider.getUserId(claims);
            String username = jwtTokenProvider.getUsername(claims);
            LoginUser loginUser = new LoginUser(userId, username);
            UsernamePasswordAuthenticationToken authentication =
                    new UsernamePasswordAuthenticationToken(loginUser, null, loginUser.getAuthorities());
            authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
            SecurityContextHolder.getContext().setAuthentication(authentication);
            filterChain.doFilter(request, response);
        } catch (BusinessException e) {
            SecurityContextHolder.clearContext();
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.setCharacterEncoding(StandardCharsets.UTF_8.name());
            response.setContentType(MediaType.APPLICATION_JSON_VALUE);
            securityJsonWriter.write(response, e.getCode(), e.getMessage());
        }
    }

    private String resolveToken(HttpServletRequest request) {
        String header = request.getHeader(jwtProperties.getHeader());
        if (!StringUtils.hasText(header)) {
            return null;
        }
        String prefix = jwtProperties.getTokenPrefix();
        if (header.startsWith(prefix)) {
            return header.substring(prefix.length()).trim();
        }
        return null;
    }
}
