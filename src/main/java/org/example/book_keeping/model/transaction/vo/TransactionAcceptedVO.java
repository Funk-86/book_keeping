package org.example.book_keeping.model.transaction.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 同步成功入库的账单项。
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TransactionAcceptedVO {

    /** 客户端去重键，用于回写本地记录 */
    private String clientDedupKey;

    /** 服务端账单主键 */
    private Long serverId;

    /** 智能分类 ID，可能为 null（未分类） */
    private Long categoryId;

    /** 0=正常 */
    private Integer status;
}
