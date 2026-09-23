package org.example.book_keeping.config.security;

import jakarta.servlet.http.HttpServletResponse;
import org.example.book_keeping.common.result.Result;
import org.example.book_keeping.common.result.ResultCode;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import tools.jackson.databind.json.JsonMapper;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

/**
 * Security 相关响应统一写出 JSON（Result 结构）。
 */
@Component
public class SecurityJsonWriter {

    private final JsonMapper jsonMapper = JsonMapper.builder().build();

    /**
     * 写出业务失败 JSON。
     *
     * @param response 响应
     * @param code     业务码
     * @param message  提示
     */
    public void write(HttpServletResponse response, int code, String message) throws IOException {
        response.setCharacterEncoding(StandardCharsets.UTF_8.name());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.getWriter().write(jsonMapper.writeValueAsString(Result.fail(code, message)));
    }

    /**
     * 按 ResultCode 写出失败 JSON。
     */
    public void write(HttpServletResponse response, ResultCode resultCode) throws IOException {
        write(response, resultCode.getCode(), resultCode.getMessage());
    }
}
