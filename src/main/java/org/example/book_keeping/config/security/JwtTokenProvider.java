package org.example.book_keeping.config.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SignatureException;
import lombok.RequiredArgsConstructor;
import org.example.book_keeping.common.exception.BusinessException;
import org.example.book_keeping.common.result.ResultCode;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

/**
 * JWT 签发与解析。
 */
@Component
@RequiredArgsConstructor
public class JwtTokenProvider {

    private static final String CLAIM_USER_ID = "uid";
    private static final String CLAIM_USERNAME = "uname";

    private final JwtProperties jwtProperties;

    /**
     * 签发 Token。
     *
     * @param userId   用户ID
     * @param username 用户名
     * @return JWT 字符串
     */
    public String createToken(Long userId, String username) {
        long now = System.currentTimeMillis();
        Date issuedAt = new Date(now);
        Date expireAt = new Date(now + jwtProperties.getExpireSeconds() * 1000L);
        return Jwts.builder()
                .subject(String.valueOf(userId))
                .claim(CLAIM_USER_ID, userId)
                .claim(CLAIM_USERNAME, username)
                .issuedAt(issuedAt)
                .expiration(expireAt)
                .signWith(signingKey())
                .compact();
    }

    /**
     * 解析并校验 Token，失败抛出业务异常。
     *
     * @param token JWT
     * @return Claims
     */
    public Claims parseClaims(String token) {
        try {
            return Jwts.parser()
                    .verifyWith(signingKey())
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();
        } catch (ExpiredJwtException e) {
            throw new BusinessException(ResultCode.TOKEN_EXPIRED);
        } catch (MalformedJwtException | SignatureException | IllegalArgumentException e) {
            throw new BusinessException(ResultCode.TOKEN_INVALID);
        }
    }

    /**
     * 从 Claims 取用户ID。
     */
    public Long getUserId(Claims claims) {
        Object uid = claims.get(CLAIM_USER_ID);
        if (uid instanceof Number number) {
            return number.longValue();
        }
        return Long.valueOf(claims.getSubject());
    }

    /**
     * 从 Claims 取用户名。
     */
    public String getUsername(Claims claims) {
        Object uname = claims.get(CLAIM_USERNAME);
        return uname == null ? null : String.valueOf(uname);
    }

    /**
     * 配置的过期秒数，供登录响应返回 expiresIn。
     */
    public long getExpireSeconds() {
        return jwtProperties.getExpireSeconds();
    }

    private SecretKey signingKey() {
        byte[] keyBytes = jwtProperties.getSecret().getBytes(StandardCharsets.UTF_8);
        return Keys.hmacShaKeyFor(keyBytes);
    }
}
