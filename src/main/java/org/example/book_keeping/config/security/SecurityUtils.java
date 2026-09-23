package org.example.book_keeping.config.security;

import org.example.book_keeping.common.exception.BusinessException;
import org.example.book_keeping.common.result.ResultCode;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

/**
 * 从 SecurityContext 读取当前登录用户。
 */
public final class SecurityUtils {

    private SecurityUtils() {
    }

    /**
     * 当前用户ID；未登录抛 {@link ResultCode#UNAUTHORIZED}。
     */
    public static Long getCurrentUserId() {
        LoginUser loginUser = getLoginUser();
        return loginUser.getUserId();
    }

    /**
     * 当前登录用户；未登录抛业务异常。
     */
    public static LoginUser getLoginUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !(authentication.getPrincipal() instanceof LoginUser loginUser)) {
            throw new BusinessException(ResultCode.UNAUTHORIZED);
        }
        return loginUser;
    }

    /**
     * 是否已登录。
     */
    public static boolean isAuthenticated() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return authentication != null && authentication.getPrincipal() instanceof LoginUser;
    }
}
