package org.example.book_keeping.model.transaction.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.example.book_keeping.common.dto.PageQuery;

import java.time.LocalDateTime;

@EqualsAndHashCode(callSuper = true)
@Data
public class TransactionListDTO extends PageQuery {
    @NotNull(message = "开始日期不能为空")
    private LocalDateTime from;
    @NotNull(message = "结束日期不能为空")
    private LocalDateTime to;
    private Integer status;
    private Integer type;
}
