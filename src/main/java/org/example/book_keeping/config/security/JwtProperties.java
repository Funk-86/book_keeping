package org.example.book_keeping.config.security;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * JWT 配置项，对应 yml 中 book-keeping.jwt。
 */
@Data
@Component
@ConfigurationProperties(prefix = "book-keeping.jwt")
public class JwtProperties {

    /** HS256 密钥 */
    private String secret;

    /** 过期秒数 */
    private long expireSeconds = 604800L;

    /** Authorization 头里的前缀，默认 "Bearer " */
    private String tokenPrefix = "Bearer ";

    /** 存放 Token 的请求头名 */
    private String header = "Authorization";
}
