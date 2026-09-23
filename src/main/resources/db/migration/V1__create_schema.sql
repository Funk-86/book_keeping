-- V1: 核心表结构（字符集 utf8mb4；时间字段语义为北京时间 Asia/Shanghai）
-- 说明：入库 createAt/updateAt 由业务层按北京时间写入；库会话时区见 application.properties (+08:00)

CREATE TABLE `user` (
    `id`            BIGINT       NOT NULL AUTO_INCREMENT COMMENT '用户ID',
    `username`      VARCHAR(64)  NOT NULL COMMENT '用户名',
    `password_hash` VARCHAR(255) NOT NULL COMMENT 'BCrypt 密码哈希',
    `created_at`    DATETIME     NOT NULL COMMENT '创建时间（北京时间）',
    `updated_at`    DATETIME     NOT NULL COMMENT '更新时间（北京时间）',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_user_username` (`username`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='用户表';

CREATE TABLE `category` (
    `id`         BIGINT      NOT NULL AUTO_INCREMENT COMMENT '分类ID',
    `name`       VARCHAR(64) NOT NULL COMMENT '分类名称，如餐饮/交通',
    `parent_id`  BIGINT               DEFAULT NULL COMMENT '父分类ID，可空',
    `icon`       VARCHAR(64)          DEFAULT NULL COMMENT '前端图标 key',
    `created_at` DATETIME    NOT NULL COMMENT '创建时间（北京时间）',
    `updated_at` DATETIME    NOT NULL COMMENT '更新时间（北京时间）',
    PRIMARY KEY (`id`),
    KEY `idx_category_parent` (`parent_id`),
    CONSTRAINT `fk_category_parent`
        FOREIGN KEY (`parent_id`) REFERENCES `category` (`id`)
        ON DELETE SET NULL ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='账单分类';

CREATE TABLE `merchant_category_rule` (
    `id`          BIGINT       NOT NULL AUTO_INCREMENT COMMENT '规则ID',
    `keyword`     VARCHAR(128) NOT NULL COMMENT '商户关键词，如美团/星巴克',
    `category_id` BIGINT       NOT NULL COMMENT '映射分类ID',
    `priority`    INT          NOT NULL DEFAULT 0 COMMENT '优先级，越大越优先',
    `created_at`  DATETIME     NOT NULL COMMENT '创建时间（北京时间）',
    `updated_at`  DATETIME     NOT NULL COMMENT '更新时间（北京时间）',
    PRIMARY KEY (`id`),
    KEY `idx_mcr_keyword` (`keyword`),
    KEY `idx_mcr_priority` (`priority`),
    CONSTRAINT `fk_mcr_category`
        FOREIGN KEY (`category_id`) REFERENCES `category` (`id`)
        ON DELETE RESTRICT ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='商户智能分类规则';

CREATE TABLE `transaction` (
    `id`               BIGINT         NOT NULL AUTO_INCREMENT COMMENT '账单ID',
    `user_id`          BIGINT         NOT NULL COMMENT '所属用户',
    `amount`           DECIMAL(12, 2) NOT NULL COMMENT '金额',
    `type`             TINYINT        NOT NULL COMMENT '1=支出 2=收入',
    `merchant`         VARCHAR(128)   NOT NULL COMMENT '商户/交易对象',
    `category_id`      BIGINT                  DEFAULT NULL COMMENT '智能分类结果',
    `source`           VARCHAR(32)    NOT NULL COMMENT '来源：wechat/alipay/unionpay/bank',
    `trade_time`       DATETIME       NOT NULL COMMENT '交易时间（北京时间）',
    `client_dedup_key` VARCHAR(128)   NOT NULL COMMENT '客户端去重键',
    `status`           TINYINT        NOT NULL DEFAULT 0 COMMENT '0=正常 1=疑似重复',
    `created_at`       DATETIME       NOT NULL COMMENT '创建时间（北京时间）',
    `updated_at`       DATETIME       NOT NULL COMMENT '更新时间（北京时间）',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_user_dedup` (`user_id`, `client_dedup_key`),
    KEY `idx_tx_user_trade_time` (`user_id`, `trade_time`),
    KEY `idx_tx_category` (`category_id`),
    KEY `idx_tx_status` (`user_id`, `status`),
    CONSTRAINT `fk_tx_user`
        FOREIGN KEY (`user_id`) REFERENCES `user` (`id`)
        ON DELETE CASCADE ON UPDATE CASCADE,
    CONSTRAINT `fk_tx_category`
        FOREIGN KEY (`category_id`) REFERENCES `category` (`id`)
        ON DELETE SET NULL ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='账单交易';

CREATE TABLE `parse_rule` (
    `id`             BIGINT       NOT NULL AUTO_INCREMENT COMMENT '规则ID',
    `package_name`   VARCHAR(128) NOT NULL COMMENT '通知来源包名，如 com.tencent.mm',
    `source_code`    VARCHAR(32)  NOT NULL COMMENT '来源编码：wechat/alipay/unionpay/bank',
    `pattern`        VARCHAR(512) NOT NULL COMMENT '正则表达式（UTF-8）',
    `amount_group`   INT          NOT NULL COMMENT '金额捕获组序号',
    `merchant_group` INT          NOT NULL COMMENT '商户捕获组序号',
    `type_hint`      TINYINT      NOT NULL DEFAULT 1 COMMENT '默认收支类型 1支出 2收入',
    `version`        INT          NOT NULL DEFAULT 1 COMMENT '规则包版本号',
    `enabled`        TINYINT      NOT NULL DEFAULT 1 COMMENT '1启用 0禁用',
    `created_at`     DATETIME     NOT NULL COMMENT '创建时间（北京时间）',
    `updated_at`     DATETIME     NOT NULL COMMENT '更新时间（北京时间）',
    PRIMARY KEY (`id`),
    KEY `idx_parse_pkg_ver` (`package_name`, `version`),
    KEY `idx_parse_enabled` (`enabled`, `version`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='通知解析规则（云端下发）';
