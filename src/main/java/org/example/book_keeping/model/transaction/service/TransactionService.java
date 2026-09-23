package org.example.book_keeping.model.transaction.service;

import org.example.book_keeping.model.transaction.dto.TransactionSyncRequest;
import org.example.book_keeping.model.transaction.vo.TransactionSyncResultVO;

public interface TransactionService {

    /**
     * 批量同步账单：去重 + 智能分类 + 入库。
     *
     * @param request 批量账单
     * @return accepted / duplicates / suspects
     */
    TransactionSyncResultVO sync(TransactionSyncRequest request);
}
