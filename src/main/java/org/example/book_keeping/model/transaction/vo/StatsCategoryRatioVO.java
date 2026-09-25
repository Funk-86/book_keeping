package org.example.book_keeping.model.transaction.vo;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class StatsCategoryRatioVO {
    private LocalDateTime from;
    private LocalDateTime to;
    private Integer type;
    private BigDecimal totalAmount;
    private List<StatsItemVO> items;
}
