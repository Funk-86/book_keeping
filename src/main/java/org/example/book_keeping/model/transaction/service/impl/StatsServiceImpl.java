package org.example.book_keeping.model.transaction.service.impl;

import lombok.RequiredArgsConstructor;
import org.example.book_keeping.common.exception.BusinessException;
import org.example.book_keeping.config.security.SecurityUtils;
import org.example.book_keeping.model.transaction.dto.StatsDTO;
import org.example.book_keeping.model.transaction.mapper.TransactionMapper;
import org.example.book_keeping.model.transaction.service.StatsService;
import org.example.book_keeping.model.transaction.vo.StatsSummaryVO;
import org.example.book_keeping.model.transaction.vo.StatsVO;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class StatsServiceImpl implements StatsService {
    private final TransactionMapper transactionMapper;

    @Override
    public StatsSummaryVO summary(StatsDTO dto) {
        if (dto.getGranularity() == null || dto.getGranularity().isBlank()) {
            throw new BusinessException("granularity 不能为空");
        }

        if (!"day".equals(dto.getGranularity().trim()) && !"week".equals(dto.getGranularity().trim()) && !"month".equals(dto.getGranularity().trim()) && !"year".equals(dto.getGranularity().trim())) {
            throw new BusinessException("传入的参数不正确");
        }

        if (dto.getFrom() == null) {
            throw new BusinessException("开始日期不能为空");
        }

        if (dto.getTo() == null) {
            throw new BusinessException("结束日期不能为空");
        }

        boolean isAfter = dto.getFrom().isAfter(dto.getTo());
        if (isAfter) {
            throw new BusinessException("开始时间不能晚于结束时间");
        }

        Long userId = SecurityUtils.getCurrentUserId();

        //支出
        BigDecimal totalExpense = transactionMapper.sumAmount(userId, 1, dto.getFrom(), dto.getTo());
        //收入
        BigDecimal totalIncome = transactionMapper.sumAmount(userId, 2, dto.getFrom(), dto.getTo());
        //净利润
        BigDecimal net = totalIncome.subtract(totalExpense);

        DateTimeFormatter formatter = switch (dto.getGranularity().trim()) {
            case "day"   -> DateTimeFormatter.ofPattern("yyyy-MM-dd");
            case "week"  -> DateTimeFormatter.ofPattern("YYYY-'W'ww", Locale.UK);
            case "month" -> DateTimeFormatter.ofPattern("yyyy-MM");
            case "year"  -> DateTimeFormatter.ofPattern("yyyy");
            default      -> throw new BusinessException("传入的参数不正确");
        };
        String label = dto.getFrom().toLocalDate().format(formatter);

        return toSummaryVO(dto, totalExpense, totalIncome, net, label);
    }

    private StatsSummaryVO toSummaryVO(StatsDTO dto, BigDecimal totalExpense, BigDecimal totalIncome, BigDecimal net, String label) {
        StatsSummaryVO vo = new StatsSummaryVO();
        vo.setGranularity(dto.getGranularity());
        vo.setFrom(dto.getFrom());
        vo.setTo(dto.getTo());
        vo.setTotalExpense(totalExpense);
        vo.setTotalIncome(totalIncome);
        vo.setNet(net);
        vo.setBuckets(toVO(totalExpense, totalIncome, label));
        return vo;
    }

    private List<StatsVO> toVO(BigDecimal totalExpense, BigDecimal totalIncome, String label) {
        List<StatsVO> vos = new ArrayList<>();
        StatsVO vo = new StatsVO();
        vo.setLabel(label);
        vo.setExpense(totalExpense != null ? totalExpense : BigDecimal.ZERO);
        vo.setIncome(totalIncome != null ? totalIncome : BigDecimal.ZERO);
        vos.add(vo);
        return vos;
    }
}
