package org.example.book_keeping.model.category.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import lombok.RequiredArgsConstructor;
import org.example.book_keeping.model.category.entity.MerchantCategoryRule;
import org.example.book_keeping.model.category.mapper.MerchantCategoryRuleMapper;
import org.example.book_keeping.model.category.service.CategoryService;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.Comparator;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CategoryServiceImpl implements CategoryService {

    /** 未分类（种子数据 category.id = 8） */
    public static final long UNCATEGORIZED_ID = 8L;

    private final MerchantCategoryRuleMapper merchantCategoryRuleMapper;

    @Override
    public Long matchCategoryId(String merchant) {
        if (!StringUtils.hasText(merchant)) {
            return UNCATEGORIZED_ID;
        }
        List<MerchantCategoryRule> rules = merchantCategoryRuleMapper.selectList(
                new LambdaQueryWrapper<MerchantCategoryRule>()
                        .orderByDesc(MerchantCategoryRule::getPriority)
        );
        return rules.stream()
                .sorted(Comparator.comparing(
                        MerchantCategoryRule::getPriority,
                        Comparator.nullsLast(Comparator.reverseOrder())))
                .filter(rule -> merchant.contains(rule.getKeyword()))
                .map(MerchantCategoryRule::getCategoryId)
                .findFirst()
                .orElse(UNCATEGORIZED_ID);
    }
}
