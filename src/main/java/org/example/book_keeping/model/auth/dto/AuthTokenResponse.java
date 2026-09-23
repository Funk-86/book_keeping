package org.example.book_keeping.model.auth.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AuthTokenResponse {

    private String token;

    private String tokenType;

    /** 过期秒数 */
    private long expiresIn;

    private Long userId;

    private String username;
}
