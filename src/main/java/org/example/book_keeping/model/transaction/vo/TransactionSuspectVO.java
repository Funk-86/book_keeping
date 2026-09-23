package org.example.book_keeping.model.transaction.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 疑似重复（去重键不同，但时间/金额/商户冲突）。
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TransactionSuspectVO {

    private String clientDedupKey;

    /** 本条入库后的服务端 ID */
    private Long serverId;

    /** 与之冲突的已有账单 ID */
    private Long conflictServerId;

    /** 1=疑似重复 */
    private Integer status;
}
