package org.example.book_keeping.model.transaction.vo;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class StatsSummaryVO {
    private String granularity;
    private LocalDateTime from;
    private LocalDateTime to;
    private BigDecimal totalExpense;
    private BigDecimal totalIncome;
    private BigDecimal net;
    private List<StatsVO> buckets;
}
