package org.example.book_keeping.model.transaction.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

import java.util.List;

/**
 * 账单批量同步请求体。
 */
@Data
public class TransactionSyncDTO {

    @NotEmpty(message = "账单列表不能为空")
    @Valid
    private List<TransactionDTO> transactions;
}
