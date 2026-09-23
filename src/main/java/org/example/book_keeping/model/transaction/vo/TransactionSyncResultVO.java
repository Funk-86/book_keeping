package org.example.book_keeping.model.transaction.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

/**
 * 账单批量同步响应 data。
 * 对应 JSON：
 * {
 *   "accepted": [...],
 *   "duplicates": [...],
 *   "suspects": [...]
 * }
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TransactionSyncResultVO {

    /** 新入库的账单 */
    @Builder.Default
    private List<TransactionAcceptedVO> accepted = new ArrayList<>();

    /** 精确重复，未再次入库 */
    @Builder.Default
    private List<TransactionDuplicateVO> duplicates = new ArrayList<>();

    /** 疑似重复，已入库但 status=1，需前端让用户确认 */
    @Builder.Default
    private List<TransactionSuspectVO> suspects = new ArrayList<>();
}
