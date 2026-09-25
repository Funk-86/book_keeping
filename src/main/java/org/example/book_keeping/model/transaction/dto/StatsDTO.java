package org.example.book_keeping.model.transaction.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class StatsDTO {
    @NotBlank(message = "granularity 不能为空")
    private String granularity;
    @NotNull(message = "开始时间不能为空")
    private LocalDateTime from;
    @NotNull(message = "结束时间不能为空")
    private LocalDateTime to;
}
