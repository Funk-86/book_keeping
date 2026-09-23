package org.example.book_keeping.model.transaction.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.book_keeping.common.result.Result;
import org.example.book_keeping.model.transaction.dto.TransactionSyncRequest;
import org.example.book_keeping.model.transaction.service.TransactionService;
import org.example.book_keeping.model.transaction.vo.TransactionSyncResultVO;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 账单接口。完整路径：/api/transactions/**
 */
@RestController
@RequestMapping("/transactions")
@RequiredArgsConstructor
public class TransactionController {

    private final TransactionService transactionService;

    @PostMapping("/sync")
    public Result<TransactionSyncResultVO> sync(@Valid @RequestBody TransactionSyncRequest request) {
        return Result.success(transactionService.sync(request));
    }
}
