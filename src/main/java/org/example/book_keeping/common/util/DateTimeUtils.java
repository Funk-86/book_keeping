package org.example.book_keeping.common.util;

import java.time.LocalDateTime;
import java.time.ZoneId;

/**
 * 北京时间工具类。
 * createAt / updateAt 入库统一使用 Asia/Shanghai。
 */
public final class DateTimeUtils {

    public static final ZoneId BEIJING = ZoneId.of("Asia/Shanghai");

    private DateTimeUtils() {
    }

    /**
     * 当前北京时间。
     *
     * @return LocalDateTime（Asia/Shanghai）
     */
    public static LocalDateTime nowBeijing() {
        return LocalDateTime.now(BEIJING);
    }
}
