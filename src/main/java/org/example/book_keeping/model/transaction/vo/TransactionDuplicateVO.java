package org.example.book_keeping.model.transaction.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 精确重复（同一 user + clientDedupKey 已存在）。
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TransactionDuplicateVO {

    private String clientDedupKey;

    /** 已存在的服务端账单 ID */
    private Long serverId;

    /** 原因，如 exact */
    private String reason;
}
