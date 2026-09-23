-- =============================================================================
-- 手动执行一次：创建数据库（utf8mb4）
-- 用法：mysql -u root -p < db/init/00_create_database.sql
-- 然后再启动 Spring Boot，由 Flyway 执行 db/migration 下的版本脚本
-- =============================================================================

CREATE DATABASE IF NOT EXISTS `book_keeping`
  DEFAULT CHARACTER SET utf8mb4
  DEFAULT COLLATE utf8mb4_unicode_ci;

-- 可选：为应用单独建用户（按需修改密码）
-- CREATE USER IF NOT EXISTS 'book_keeping'@'%' IDENTIFIED BY 'book_keeping';
-- GRANT ALL PRIVILEGES ON `book_keeping`.* TO 'book_keeping'@'%';
-- FLUSH PRIVILEGES;

USE `book_keeping`;
