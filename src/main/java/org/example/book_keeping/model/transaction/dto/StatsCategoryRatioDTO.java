package org.example.book_keeping.model.transaction.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class StatsCategoryRatioDTO {
    @NotNull(message = "开始时间不能为空")
    private LocalDateTime from;
    @NotNull(message = "结束时间不能为空")
    private LocalDateTime to;
    private Integer type;
}
