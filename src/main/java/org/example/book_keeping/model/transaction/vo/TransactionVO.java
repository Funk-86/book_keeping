package org.example.book_keeping.model.transaction.vo;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class TransactionVO {
    private Long id;
    private BigDecimal amount;
    private Integer type;
    private String merchant;
    private Long categoryId;
    private String categoryName;
    private String source;
    private LocalDateTime tradeTime;
    private Integer status;
}
