package org.example.book_keeping.model.transaction.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.RequiredArgsConstructor;
import org.example.book_keeping.common.exception.BusinessException;
import org.example.book_keeping.common.result.PageResult;
import org.example.book_keeping.common.result.ResultCode;
import org.example.book_keeping.config.security.SecurityUtils;
import org.example.book_keeping.model.category.entity.Category;
import org.example.book_keeping.model.category.mapper.CategoryMapper;
import org.example.book_keeping.model.category.service.CategoryService;
import org.example.book_keeping.model.transaction.dto.TransactionDTO;
import org.example.book_keeping.model.transaction.dto.TransactionListDTO;
import org.example.book_keeping.model.transaction.dto.TransactionSyncDTO;
import org.example.book_keeping.model.transaction.entity.Transaction;
import org.example.book_keeping.model.transaction.mapper.TransactionMapper;
import org.example.book_keeping.model.transaction.service.TransactionService;
import org.example.book_keeping.model.transaction.vo.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TransactionServiceImpl implements TransactionService {

    private static final long SUSPECT_WINDOW_MINUTES = 5L;
    private static final int STATUS_NORMAL = 0;
    private static final int STATUS_SUSPECT = 1;
    private static final int TYPE_EXPENSE = 1;
    private static final int TYPE_INCOME = 2;

    private final TransactionMapper transactionMapper;
    private final CategoryService categoryService;
    private final CategoryMapper categoryMapper;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public TransactionSyncResultVO sync(TransactionSyncDTO request) {
        List<TransactionDTO> list = request.getTransactions();
        if (list == null || list.isEmpty()) {
            throw new BusinessException(ResultCode.SYNC_EMPTY);
        }

        Long userId = SecurityUtils.getCurrentUserId();
        TransactionSyncResultVO resultVO = new TransactionSyncResultVO();

        for (TransactionDTO dto : list) {
            validateDto(dto);
            handleOne(userId, dto, resultVO);
        }
        return resultVO;
    }

    @Override
    public PageResult<TransactionVO> list(TransactionListDTO dto) {
        Long userId = SecurityUtils.getCurrentUserId();
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

        LambdaQueryWrapper<Transaction> queryWrapper = new LambdaQueryWrapper<Transaction>()
                .eq(Transaction::getUserId, userId)
                .ge(Transaction::getTradeTime, dto.getFrom())
                .le(Transaction::getTradeTime, dto.getTo())
                .eq(dto.getType() != null, Transaction::getType, dto.getType())
                .eq(dto.getStatus() != null, Transaction::getStatus, dto.getStatus())
                .orderByDesc(Transaction::getTradeTime);

        IPage<Transaction> iPage = transactionMapper.selectPage(
                new Page<>(dto.getPageNum(), dto.getPageSize()), queryWrapper);

        List<Transaction> list = iPage.getRecords();
        if (list == null || list.isEmpty()) {
            return PageResult.empty(dto.getPageNum(), dto.getPageSize());
        }

        Map<Long, String> categoryNameMap = loadCategoryNameMap(list);


        List<TransactionVO> voList = toVO(list, categoryNameMap);
        PageResult<TransactionVO> pageResult = new PageResult<>();
        pageResult.setList(voList);
        pageResult.setTotal(iPage.getTotal());
        pageResult.setPageNum(dto.getPageNum());
        pageResult.setPageSize(dto.getPageSize());
        pageResult.setPages(iPage.getPages());

        return pageResult;
    }

    private void handleOne(Long userId, TransactionDTO dto, TransactionSyncResultVO resultVO) {
        Transaction exact = findByDedupKey(userId, dto.getClientDedupKey());
        if (exact != null) {
            resultVO.getDuplicates().add(toDuplicateVO(exact, dto));
            return;
        }

        Transaction conflict = findSuspectConflict(userId, dto);
        if (conflict != null) {
            Transaction entity = toEntity(userId, dto, STATUS_SUSPECT);
            transactionMapper.insert(entity);
            resultVO.getSuspects().add(toSuspectVO(entity, dto, conflict.getId()));
            return;
        }

        Transaction entity = toEntity(userId, dto, STATUS_NORMAL);
        transactionMapper.insert(entity);
        resultVO.getAccepted().add(toAcceptedVO(entity, dto));
    }

    private Transaction findByDedupKey(Long userId, String clientDedupKey) {
        return transactionMapper.selectOne(new LambdaQueryWrapper<Transaction>()
                .eq(Transaction::getUserId, userId)
                .eq(Transaction::getClientDedupKey, clientDedupKey)
                .last("LIMIT 1"));
    }

    private Transaction findSuspectConflict(Long userId, TransactionDTO dto) {
        LocalDateTime tradeTime = dto.getTradeTime();
        LocalDateTime from = tradeTime.minusMinutes(SUSPECT_WINDOW_MINUTES);
        LocalDateTime to = tradeTime.plusMinutes(SUSPECT_WINDOW_MINUTES);
        return transactionMapper.selectOne(new LambdaQueryWrapper<Transaction>()
                .eq(Transaction::getUserId, userId)
                .eq(Transaction::getAmount, dto.getAmount())
                .eq(Transaction::getMerchant, dto.getMerchant())
                .between(Transaction::getTradeTime, from, to)
                .ne(Transaction::getClientDedupKey, dto.getClientDedupKey())
                .orderByDesc(Transaction::getId)
                .last("LIMIT 1"));
    }

    private Transaction toEntity(Long userId, TransactionDTO dto, int status) {
        Transaction entity = new Transaction();
        entity.setUserId(userId);
        entity.setAmount(dto.getAmount());
        entity.setType(dto.getType());
        entity.setMerchant(dto.getMerchant());
        entity.setSource(dto.getSource());
        entity.setTradeTime(dto.getTradeTime());
        entity.setClientDedupKey(dto.getClientDedupKey());
        entity.setCategoryId(categoryService.matchCategoryId(dto.getMerchant()));
        entity.setStatus(status);
        return entity;
    }

    private TransactionAcceptedVO toAcceptedVO(Transaction entity, TransactionDTO dto) {
        TransactionAcceptedVO vo = new TransactionAcceptedVO();
        vo.setClientDedupKey(dto.getClientDedupKey());
        vo.setServerId(entity.getId());
        vo.setCategoryId(entity.getCategoryId());
        vo.setStatus(entity.getStatus());
        return vo;
    }

    private TransactionDuplicateVO toDuplicateVO(Transaction exist, TransactionDTO dto) {
        TransactionDuplicateVO vo = new TransactionDuplicateVO();
        vo.setClientDedupKey(dto.getClientDedupKey());
        vo.setServerId(exist.getId());
        vo.setReason("exact");
        return vo;
    }

    private TransactionSuspectVO toSuspectVO(Transaction entity, TransactionDTO dto, Long conflictServerId) {
        TransactionSuspectVO vo = new TransactionSuspectVO();
        vo.setClientDedupKey(dto.getClientDedupKey());
        vo.setServerId(entity.getId());
        vo.setConflictServerId(conflictServerId);
        vo.setStatus(entity.getStatus());
        return vo;
    }

    private void validateDto(TransactionDTO dto) {
        if (dto.getAmount() == null || dto.getAmount().compareTo(BigDecimal.ZERO) <= 0) {
            throw new BusinessException(ResultCode.AMOUNT_INVALID);
        }
        if (dto.getType() == null
                || (dto.getType() != TYPE_EXPENSE && dto.getType() != TYPE_INCOME)) {
            throw new BusinessException(ResultCode.TYPE_INVALID);
        }
    }

    private List<TransactionVO> toVO(List<Transaction> transaction, Map<Long, String> categoryNameMap) {

        return transaction.stream().map(t -> {
            TransactionVO vo = new TransactionVO();
            vo.setId(t.getId());
            vo.setAmount(t.getAmount());
            vo.setType(t.getType());
            vo.setMerchant(t.getMerchant());
            vo.setCategoryId(t.getCategoryId());
            vo.setCategoryName(resolveCategoryName(t.getCategoryId(), categoryNameMap));
            vo.setSource(t.getSource());
            vo.setTradeTime(t.getTradeTime());
            vo.setStatus(t.getStatus());
            return vo;
        }).collect(Collectors.toList());
    }

    private String resolveCategoryName(Long categoryId, Map<Long, String> categoryNameMap) {
        if (categoryId == null) {
            return "未分类";
        }
        return categoryNameMap.getOrDefault(categoryId, "未分类");
    }

    private Map<Long, String> loadCategoryNameMap(List<Transaction> list) {
        Set<Long> ids = list.stream()
                .map(Transaction::getCategoryId)
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());

        if (ids.isEmpty()) {
            return Collections.emptyMap();
        }

        return categoryMapper.selectBatchIds(ids)
                .stream()
                .filter(c -> c.getName() != null)
                .collect(Collectors.toMap(Category::getId, Category::getName, (a, b) -> a));
    }
}
