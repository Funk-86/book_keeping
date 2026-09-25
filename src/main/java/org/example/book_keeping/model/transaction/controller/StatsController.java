package org.example.book_keeping.model.transaction.controller;


import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.book_keeping.common.result.Result;
import org.example.book_keeping.model.transaction.dto.StatsDTO;
import org.example.book_keeping.model.transaction.service.StatsService;
import org.example.book_keeping.model.transaction.vo.StatsSummaryVO;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/stats")
public class StatsController {
    private final StatsService statsService;

    @GetMapping("/summary")
    public Result<StatsSummaryVO> summary(@Valid StatsDTO dto) {
        return Result.success(statsService.summary(dto));
    }
}
