package org.example.book_keeping.common.controller;

import org.example.book_keeping.common.result.Result;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
 * 健康检查（无需登录）。
 */
@RestController
public class HealthController {

    @GetMapping("/health")
    public Result<Map<String, String>> health() {
        return Result.success(Map.of("status", "UP"));
    }
}
