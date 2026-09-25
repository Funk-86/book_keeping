package org.example.book_keeping.model.transaction.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.book_keeping.common.dto.PageQuery;
import org.example.book_keeping.common.result.PageResult;
import org.example.book_keeping.common.result.Result;
import org.example.book_keeping.model.transaction.dto.TransactionListDTO;
import org.example.book_keeping.model.transaction.dto.TransactionSyncDTO;
import org.example.book_keeping.model.transaction.service.TransactionService;
import org.example.book_keeping.model.transaction.vo.TransactionSyncResultVO;
import org.example.book_keeping.model.transaction.vo.TransactionVO;
import org.springframework.web.bind.annotation.*;

/**
 * 账单接口。完整路径：/api/transactions/**
 */
@RestController
@RequestMapping("/transactions")
@RequiredArgsConstructor
public class TransactionController {

    private final TransactionService transactionService;

    @PostMapping("/sync")
    public Result<TransactionSyncResultVO> sync(@Valid @RequestBody TransactionSyncDTO dto) {
        return Result.success(transactionService.sync(dto));
    }

    @GetMapping
    public Result<PageResult<TransactionVO>> list(@Valid TransactionListDTO dto) {
        return Result.success(transactionService.list(dto));
    }
}
