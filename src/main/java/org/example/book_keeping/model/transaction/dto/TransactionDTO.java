package org.example.book_keeping.model.transaction.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 单条待同步账单（请求体中 transactions 的元素）。
 */
@Data
public class TransactionDTO {

    @NotBlank(message = "clientDedupKey 不能为空")
    private String clientDedupKey;

    @NotNull(message = "金额不能为空")
    @DecimalMin(value = "0.01", message = "金额必须大于 0")
    private BigDecimal amount;

    /** 1=支出 2=收入 */
    @NotNull(message = "收支类型不能为空")
    private Integer type;

    @NotBlank(message = "商户不能为空")
    private String merchant;

    @NotNull(message = "交易时间不能为空")
    private LocalDateTime tradeTime;

    /** wechat / alipay / unionpay / bank */
    @NotBlank(message = "来源不能为空")
    private String source;
}
