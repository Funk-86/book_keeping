package org.example.book_keeping.common.result;

import lombok.Getter;

@Getter
public enum ResultCode {

    SUCCESS(0, "ok"),

    USER_ALREADY_EXISTS(1001, "用户名已存在"),
    USER_NOT_FOUND(1002, "用户不存在"),
    BAD_CREDENTIALS(1003, "用户名或密码错误"),
    UNAUTHORIZED(1004, "未登录或登录已失效"),
    TOKEN_INVALID(1005, "登录凭证无效"),
    TOKEN_EXPIRED(1006, "登录已过期，请重新登录"),
    FORBIDDEN(1007, "没有访问权限"),

    PARAM_INVALID(2001, "请求参数不正确"),
    BODY_INVALID(2002, "请求数据格式错误"),
    PARAM_MISSING(2003, "缺少必填参数"),
    AMOUNT_INVALID(2004, "金额不合法"),
    TYPE_INVALID(2005, "收支类型不合法"),

    SIGN_INVALID(3001, "请求签名无效"),
    SIGN_EXPIRED(3002, "请求已过期，请重试"),
    SIGN_NONCE_REUSED(3003, "重复的请求，请勿重试过快"),
    TRANSACTION_DUPLICATE(3004, "账单已存在，请勿重复上传"),
    TRANSACTION_SUSPECT(3005, "疑似重复账单，请确认"),
    SYNC_EMPTY(3006, "没有可同步的账单"),
    RATE_LIMITED(3007, "操作过于频繁，请稍后再试"),

    TRANSACTION_NOT_FOUND(4001, "账单不存在"),
    CATEGORY_NOT_FOUND(4002, "分类不存在"),
    PARSE_RULE_NOT_FOUND(4003, "解析规则不存在"),
    STATS_RANGE_INVALID(4004, "统计时间范围不合法"),

    INTERNAL_ERROR(5000, "系统繁忙，请稍后再试"),
    DB_ERROR(5001, "数据服务异常"),
    EXTERNAL_ERROR(5002, "外部服务异常");

    private final int code;
    private final String message;

    ResultCode(int code, String message) {
        this.code = code;
        this.message = message;
    }

    public int getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }

    public static ResultCode fromCode(int code) {
        for (ResultCode value : values()) {
            if (value.code == code) {
                return value;
            }
        }
        return INTERNAL_ERROR;
    }

    public boolean isSuccess() {
        return this == SUCCESS;
    }
}
