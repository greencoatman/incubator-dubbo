/*
 * Ecommerce MVP Database Schema
 * Version: 1.0
 * Date: 2026-01-12
 */

CREATE DATABASE IF NOT EXISTS `ecommerce_mvp` DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

USE `ecommerce_mvp`;

-- ----------------------------
-- 1. 用户表 (Users)
-- ----------------------------
DROP TABLE IF EXISTS `users`;
CREATE TABLE `users` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `nickname` VARCHAR(64) DEFAULT NULL COMMENT '用户昵称',
    `avatar_url` VARCHAR(255) DEFAULT NULL COMMENT '头像地址',
    `openid` VARCHAR(64) NOT NULL COMMENT '微信OpenID',
    `mobile` VARCHAR(20) DEFAULT NULL COMMENT '手机号',
    `created_at` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updated_at` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_openid` (`openid`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户表';

-- ----------------------------
-- 2. 商品分类表 (Categories)
-- ----------------------------
DROP TABLE IF EXISTS `categories`;
CREATE TABLE `categories` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `name` VARCHAR(50) NOT NULL COMMENT '分类名称',
    `parent_id` BIGINT DEFAULT 0 COMMENT '父分类ID (0为根分类)',
    `icon_url` VARCHAR(255) DEFAULT NULL COMMENT '分类图标',
    `sort_order` INT DEFAULT 0 COMMENT '排序权重',
    `status` TINYINT DEFAULT 1 COMMENT '状态: 1-启用, 0-禁用',
    `created_at` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updated_at` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    KEY `idx_parent_id` (`parent_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='商品分类表';

-- ----------------------------
-- 3. 商品表 (Products)
-- ----------------------------
DROP TABLE IF EXISTS `products`;
CREATE TABLE `products` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `category_id` BIGINT NOT NULL COMMENT '分类ID',
    `name` VARCHAR(100) NOT NULL COMMENT '商品名称',
    `subtitle` VARCHAR(200) DEFAULT NULL COMMENT '商品副标题/卖点',
    `main_image` VARCHAR(255) NOT NULL COMMENT '商品主图',
    `price` DECIMAL(10, 2) NOT NULL COMMENT '销售价格',
    `original_price` DECIMAL(10, 2) DEFAULT NULL COMMENT '市场原价',
    `stock` INT NOT NULL DEFAULT 0 COMMENT '库存数量',
    `detail_content` LONGTEXT COMMENT '商品详情(富文本)',
    `status` TINYINT DEFAULT 1 COMMENT '状态: 1-上架, 0-下架',
    `sales` INT DEFAULT 0 COMMENT '销量',
    `created_at` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updated_at` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    KEY `idx_category_id` (`category_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='商品表';

-- ----------------------------
-- 4. 购物车表 (Cart Items)
-- ----------------------------
DROP TABLE IF EXISTS `cart_items`;
CREATE TABLE `cart_items` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `user_id` BIGINT NOT NULL COMMENT '用户ID',
    `product_id` BIGINT NOT NULL COMMENT '商品ID',
    `quantity` INT NOT NULL DEFAULT 1 COMMENT '购买数量',
    `selected` TINYINT DEFAULT 1 COMMENT '是否选中: 1-是, 0-否',
    `created_at` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updated_at` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    KEY `idx_user_id` (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='购物车表';
