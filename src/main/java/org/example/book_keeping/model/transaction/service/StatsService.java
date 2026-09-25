package org.example.book_keeping.model.transaction.service;

import org.example.book_keeping.model.transaction.dto.StatsDTO;
import org.example.book_keeping.model.transaction.vo.StatsSummaryVO;

public interface StatsService {
    StatsSummaryVO summary(StatsDTO dto);
}
