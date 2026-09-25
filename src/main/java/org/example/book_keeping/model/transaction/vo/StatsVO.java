package org.example.book_keeping.model.transaction.vo;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class StatsVO {
    private String label;
    private BigDecimal expense;
    private BigDecimal income;
}
