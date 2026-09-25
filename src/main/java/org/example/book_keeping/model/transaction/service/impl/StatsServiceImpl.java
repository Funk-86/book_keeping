package org.example.book_keeping.model.transaction.service.impl;

import lombok.RequiredArgsConstructor;
import org.example.book_keeping.common.exception.BusinessException;
import org.example.book_keeping.config.security.SecurityUtils;
import org.example.book_keeping.model.category.entity.Category;
import org.example.book_keeping.model.category.mapper.CategoryMapper;
import org.example.book_keeping.model.transaction.dto.StatsCategoryRatioDTO;
import org.example.book_keeping.model.transaction.dto.StatsDTO;
import org.example.book_keeping.model.transaction.mapper.TransactionMapper;
import org.example.book_keeping.model.transaction.service.StatsService;
import org.example.book_keeping.model.transaction.vo.StatsCategoryRatioVO;
import org.example.book_keeping.model.transaction.vo.StatsItemVO;
import org.example.book_keeping.model.transaction.vo.StatsSummaryVO;
import org.example.book_keeping.model.transaction.vo.StatsVO;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class StatsServiceImpl implements StatsService {
    private final TransactionMapper transactionMapper;
    private final CategoryMapper categoryMapper;

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

    @Override
    public StatsCategoryRatioVO categoryRatio(StatsCategoryRatioDTO dto) {
        int type;
        if (dto.getType() == null) {
            type = 1;
        } else {
            type = dto.getType();
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

        BigDecimal totalAmount = transactionMapper.sumAmount(userId, type, dto.getFrom(), dto.getTo());

        //查询前端传过来的时间段，根据categoryId进行分组
        List<StatsItemVO> items = transactionMapper.sumAmountByCategoryId(userId, type, dto.getFrom(), dto.getTo());

        return toCategoryRatioVO(dto, totalAmount, items, type);
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

    private StatsCategoryRatioVO toCategoryRatioVO(StatsCategoryRatioDTO dto, BigDecimal totalAmount, List<StatsItemVO> items, int type) {
        StatsCategoryRatioVO vo = new StatsCategoryRatioVO();
        vo.setFrom(dto.getFrom());
        vo.setTo(dto.getTo());
        vo.setTotalAmount(totalAmount);
        vo.setType(type);
        vo.setItems(toItemVO(items, totalAmount));
        return vo;
    }

    private List<StatsItemVO> toItemVO(List<StatsItemVO> items, BigDecimal totalAmount) {
        Set<Long> ids = items.stream()
                .map(StatsItemVO::getCategoryId)
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());
        Map<Long, String> map = ids.isEmpty()
                ? Collections.emptyMap()
                : categoryMapper.selectBatchIds(ids).stream()
                .collect(Collectors.toMap(Category::getId, Category::getName));

        for (StatsItemVO item : items) {
            item.setCategoryName(map.getOrDefault(item.getCategoryId(), "未分类"));
            item.setRatio(
                    totalAmount.compareTo(BigDecimal.ZERO) == 0
                            ? BigDecimal.ZERO
                            : item.getAmount().divide(totalAmount, 4, RoundingMode.HALF_UP)
            );
        }
        return items;
    }
}
