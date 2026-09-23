package org.example.book_keeping.model.transaction.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import lombok.RequiredArgsConstructor;
import org.example.book_keeping.common.exception.BusinessException;
import org.example.book_keeping.common.result.ResultCode;
import org.example.book_keeping.config.security.SecurityUtils;
import org.example.book_keeping.model.category.service.CategoryService;
import org.example.book_keeping.model.transaction.dto.TransactionDTO;
import org.example.book_keeping.model.transaction.dto.TransactionSyncRequest;
import org.example.book_keeping.model.transaction.entity.Transaction;
import org.example.book_keeping.model.transaction.mapper.TransactionMapper;
import org.example.book_keeping.model.transaction.service.TransactionService;
import org.example.book_keeping.model.transaction.vo.TransactionAcceptedVO;
import org.example.book_keeping.model.transaction.vo.TransactionDuplicateVO;
import org.example.book_keeping.model.transaction.vo.TransactionSuspectVO;
import org.example.book_keeping.model.transaction.vo.TransactionSyncResultVO;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

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

    @Override
    @Transactional(rollbackFor = Exception.class)
    public TransactionSyncResultVO sync(TransactionSyncRequest request) {
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
}
