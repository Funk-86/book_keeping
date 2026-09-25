package org.example.book_keeping.model.transaction.vo;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class StatsItemVO {
    private Long categoryId;
    private String categoryName;
    private BigDecimal amount;
    private BigDecimal ratio;
}
