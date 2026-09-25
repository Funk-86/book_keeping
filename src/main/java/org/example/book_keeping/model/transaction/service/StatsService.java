package org.example.book_keeping.model.transaction.service;

import org.example.book_keeping.model.transaction.dto.StatsCategoryRatioDTO;
import org.example.book_keeping.model.transaction.dto.StatsDTO;
import org.example.book_keeping.model.transaction.vo.StatsCategoryRatioVO;
import org.example.book_keeping.model.transaction.vo.StatsSummaryVO;

public interface StatsService {
    StatsSummaryVO summary(StatsDTO dto);
    StatsCategoryRatioVO categoryRatio(StatsCategoryRatioDTO dto);
}
