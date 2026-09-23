package org.example.book_keeping.model.transaction.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.example.book_keeping.common.entity.BaseEntity;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("`transaction`")
public class Transaction extends BaseEntity {

    private Long userId;

    private BigDecimal amount;

    /** 1=支出 2=收入 */
    private Integer type;

    private String merchant;

    private Long categoryId;

    /** wechat / alipay / unionpay / bank */
    private String source;

    private LocalDateTime tradeTime;

    private String clientDedupKey;

    /** 0=正常 1=疑似重复 */
    private Integer status;
}
