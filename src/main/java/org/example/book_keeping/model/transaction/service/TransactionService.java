package org.example.book_keeping.model.transaction.service;

import org.example.book_keeping.common.dto.PageQuery;
import org.example.book_keeping.common.result.PageResult;
import org.example.book_keeping.model.transaction.dto.TransactionListDTO;
import org.example.book_keeping.model.transaction.dto.TransactionSyncDTO;
import org.example.book_keeping.model.transaction.vo.TransactionSyncResultVO;
import org.example.book_keeping.model.transaction.vo.TransactionVO;

public interface TransactionService {

    TransactionSyncResultVO sync(TransactionSyncDTO dto);
    PageResult<TransactionVO> list(TransactionListDTO dto);
}
