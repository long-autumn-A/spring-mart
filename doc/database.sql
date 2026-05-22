-- =====================================================
-- 恩施土家美食电商平台 (SpringMart) 数据库设计
-- 数据库名: paper
-- =====================================================

CREATE DATABASE IF NOT EXISTS paper DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE paper;

-- ==================== 1. 用户表 ====================
DROP TABLE IF EXISTS `user`;
CREATE TABLE `user` (
    `id`            BIGINT       NOT NULL AUTO_INCREMENT  COMMENT '用户ID',
    `username`      VARCHAR(50)  NOT NULL                 COMMENT '用户名',
    `password`      VARCHAR(255) NOT NULL                 COMMENT '密码（Argon2加密）',
    `nickname`      VARCHAR(50)  DEFAULT NULL             COMMENT '昵称',
    `phone`         VARCHAR(20)  DEFAULT NULL             COMMENT '手机号',
    `email`         VARCHAR(100) DEFAULT NULL             COMMENT '邮箱',
    `avatar`        VARCHAR(255) DEFAULT NULL             COMMENT '头像URL',
    `role`          TINYINT      NOT NULL DEFAULT 0       COMMENT '角色: 0=普通用户, 1=商家, 2=管理员',
    `status`        TINYINT      NOT NULL DEFAULT 0       COMMENT '状态: 0=正常, 1=禁用',
    `created_at`    DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updated_at`    DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `deleted`       TINYINT      NOT NULL DEFAULT 0       COMMENT '逻辑删除: 0=未删除, 1=已删除',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_username` (`username`),
    UNIQUE KEY `uk_phone` (`phone`),
    KEY `idx_role` (`role`),
    KEY `idx_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户表';


-- ==================== 2. 商品分类表 ====================
DROP TABLE IF EXISTS `category`;
CREATE TABLE `category` (
    `id`            BIGINT      NOT NULL AUTO_INCREMENT  COMMENT '分类ID',
    `name`          VARCHAR(50) NOT NULL                 COMMENT '分类名称',
    `parent_id`     BIGINT      NOT NULL DEFAULT 0       COMMENT '父分类ID，0=顶级分类',
    `sort_order`    INT         NOT NULL DEFAULT 0       COMMENT '排序值（越小越靠前）',
    `icon`          VARCHAR(255) DEFAULT NULL            COMMENT '分类图标URL',
    `created_at`    DATETIME    NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updated_at`    DATETIME    NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `deleted`       TINYINT     NOT NULL DEFAULT 0       COMMENT '逻辑删除',
    PRIMARY KEY (`id`),
    KEY `idx_parent_id` (`parent_id`),
    KEY `idx_sort_order` (`sort_order`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='商品分类表';


-- ==================== 3. 店铺表 ====================
DROP TABLE IF EXISTS `shop`;
CREATE TABLE `shop` (
    `id`            BIGINT       NOT NULL AUTO_INCREMENT  COMMENT '店铺ID',
    `user_id`       BIGINT       NOT NULL                 COMMENT '商家用户ID',
    `name`          VARCHAR(100) NOT NULL                 COMMENT '店铺名称',
    `logo`          VARCHAR(255) DEFAULT NULL             COMMENT '店铺Logo',
    `banner`        VARCHAR(255) DEFAULT NULL             COMMENT '店铺横幅',
    `description`   VARCHAR(500) DEFAULT NULL             COMMENT '店铺简介',
    `phone`         VARCHAR(20)  DEFAULT NULL             COMMENT '店铺联系电话',
    `status`        TINYINT      NOT NULL DEFAULT 1       COMMENT '状态: 0=关闭, 1=营业中',
    `rating`        DECIMAL(2,1) NOT NULL DEFAULT 5.0     COMMENT '店铺评分(1.0~5.0)',
    `created_at`    DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updated_at`    DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `deleted`       TINYINT      NOT NULL DEFAULT 0       COMMENT '逻辑删除',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_user_id` (`user_id`),
    KEY `idx_status` (`status`),
    KEY `idx_rating` (`rating`),
    CONSTRAINT `fk_shop_user` FOREIGN KEY (`user_id`) REFERENCES `user`(`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='店铺表';


-- ==================== 4. 商品表 ====================
DROP TABLE IF EXISTS `product`;
CREATE TABLE `product` (
    `id`             BIGINT         NOT NULL AUTO_INCREMENT  COMMENT '商品ID',
    `name`           VARCHAR(100)   NOT NULL                 COMMENT '商品名称',
    `category_id`    BIGINT         NOT NULL                 COMMENT '所属分类ID',
    `shop_id`       BIGINT         NOT NULL DEFAULT 0       COMMENT '所属店铺ID，0=平台自营',
    `description`    VARCHAR(500)   DEFAULT NULL             COMMENT '商品简述',
    `detail`         LONGTEXT       DEFAULT NULL             COMMENT '商品详情（富文本）',
    `price`          DECIMAL(10,2)  NOT NULL                 COMMENT '售价',
    `original_price` DECIMAL(10,2)  DEFAULT NULL             COMMENT '原价（划线价）',
    `stock`          INT            NOT NULL DEFAULT 0       COMMENT '库存',
    `sales`          INT            NOT NULL DEFAULT 0       COMMENT '累计销量',
    `main_image`     VARCHAR(255)   DEFAULT NULL             COMMENT '商品主图URL',
    `status`         TINYINT        NOT NULL DEFAULT 0       COMMENT '状态: 0=下架, 1=上架',
    `is_recommended` TINYINT        NOT NULL DEFAULT 0       COMMENT '是否推荐: 0=否, 1=是',
    `created_at`     DATETIME       NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updated_at`     DATETIME       NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `deleted`        TINYINT        NOT NULL DEFAULT 0       COMMENT '逻辑删除',
    PRIMARY KEY (`id`),
    KEY `idx_category_id` (`category_id`),
    KEY `idx_shop_id` (`shop_id`),
    KEY `idx_status` (`status`),
    KEY `idx_is_recommended` (`is_recommended`),
    KEY `idx_sales` (`sales`),
    CONSTRAINT `fk_product_category` FOREIGN KEY (`category_id`) REFERENCES `category`(`id`),
    CONSTRAINT `fk_product_shop` FOREIGN KEY (`shop_id`) REFERENCES `shop`(`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='商品表';


-- ==================== 4. 商品图片表 ====================
DROP TABLE IF EXISTS `product_image`;
CREATE TABLE `product_image` (
    `id`            BIGINT       NOT NULL AUTO_INCREMENT  COMMENT '图片ID',
    `product_id`    BIGINT       NOT NULL                 COMMENT '商品ID',
    `image_url`     VARCHAR(255) NOT NULL                 COMMENT '图片URL',
    `sort_order`    INT          NOT NULL DEFAULT 0       COMMENT '排序值',
    `created_at`    DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    PRIMARY KEY (`id`),
    KEY `idx_product_id` (`product_id`),
    CONSTRAINT `fk_image_product` FOREIGN KEY (`product_id`) REFERENCES `product`(`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='商品图片表';


-- ==================== 5. 购物车表 ====================
DROP TABLE IF EXISTS `cart`;
CREATE TABLE `cart` (
    `id`            BIGINT   NOT NULL AUTO_INCREMENT  COMMENT '购物车ID',
    `user_id`       BIGINT   NOT NULL                 COMMENT '用户ID',
    `product_id`    BIGINT   NOT NULL                 COMMENT '商品ID',
    `quantity`      INT      NOT NULL DEFAULT 1       COMMENT '数量',
    `created_at`    DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updated_at`    DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_user_product` (`user_id`, `product_id`),
    KEY `idx_user_id` (`user_id`),
    CONSTRAINT `fk_cart_user` FOREIGN KEY (`user_id`) REFERENCES `user`(`id`),
    CONSTRAINT `fk_cart_product` FOREIGN KEY (`product_id`) REFERENCES `product`(`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='购物车表';


-- ==================== 6. 收货地址表 ====================
DROP TABLE IF EXISTS `address`;
CREATE TABLE `address` (
    `id`              BIGINT       NOT NULL AUTO_INCREMENT  COMMENT '地址ID',
    `user_id`         BIGINT       NOT NULL                 COMMENT '用户ID',
    `receiver_name`   VARCHAR(50)  NOT NULL                 COMMENT '收货人姓名',
    `receiver_phone`  VARCHAR(20)  NOT NULL                 COMMENT '收货人手机号',
    `province`        VARCHAR(50)  NOT NULL                 COMMENT '省',
    `city`            VARCHAR(50)  NOT NULL                 COMMENT '市',
    `district`        VARCHAR(50)  NOT NULL                 COMMENT '区/县',
    `detail`          VARCHAR(255) NOT NULL                 COMMENT '详细地址',
    `is_default`      TINYINT      NOT NULL DEFAULT 0       COMMENT '是否默认: 0=否, 1=是',
    `created_at`      DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updated_at`      DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `deleted`         TINYINT      NOT NULL DEFAULT 0       COMMENT '逻辑删除',
    PRIMARY KEY (`id`),
    KEY `idx_user_id` (`user_id`),
    CONSTRAINT `fk_address_user` FOREIGN KEY (`user_id`) REFERENCES `user`(`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='收货地址表';


-- ==================== 7. 订单表 ====================
DROP TABLE IF EXISTS `order`;
CREATE TABLE `order` (
    `id`              BIGINT         NOT NULL AUTO_INCREMENT  COMMENT '订单ID',
    `order_no`        VARCHAR(32)    NOT NULL                 COMMENT '订单编号（雪花ID + 时间戳）',
    `user_id`         BIGINT         NOT NULL                 COMMENT '用户ID',
    `receiver_name`   VARCHAR(50)    NOT NULL                 COMMENT '收货人（快照）',
    `receiver_phone`  VARCHAR(20)    NOT NULL                 COMMENT '收货电话（快照）',
    `receiver_address` VARCHAR(255)  NOT NULL                 COMMENT '收货地址（快照）',
    `total_amount`    DECIMAL(10,2)  NOT NULL                 COMMENT '订单总金额',
    `status`          TINYINT        NOT NULL DEFAULT 0       COMMENT '状态: 0=待付款, 1=待发货, 2=待收货, 3=已完成, 4=已取消',
    `pay_time`        DATETIME       DEFAULT NULL             COMMENT '付款时间',
    `deliver_time`    DATETIME       DEFAULT NULL             COMMENT '发货时间',
    `finish_time`     DATETIME       DEFAULT NULL             COMMENT '完成时间',
    `cancel_time`     DATETIME       DEFAULT NULL             COMMENT '取消时间',
    `remark`          VARCHAR(500)   DEFAULT NULL             COMMENT '买家备注',
    `created_at`      DATETIME       NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updated_at`      DATETIME       NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `deleted`         TINYINT        NOT NULL DEFAULT 0       COMMENT '逻辑删除',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_order_no` (`order_no`),
    KEY `idx_user_id` (`user_id`),
    KEY `idx_status` (`status`),
    KEY `idx_created_at` (`created_at`),
    CONSTRAINT `fk_order_user` FOREIGN KEY (`user_id`) REFERENCES `user`(`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='订单表';


-- ==================== 8. 订单明细表 ====================
DROP TABLE IF EXISTS `order_item`;
CREATE TABLE `order_item` (
    `id`             BIGINT         NOT NULL AUTO_INCREMENT  COMMENT '明细ID',
    `order_id`       BIGINT         NOT NULL                 COMMENT '订单ID',
    `product_id`     BIGINT         NOT NULL                 COMMENT '商品ID',
    `product_name`   VARCHAR(100)   NOT NULL                 COMMENT '商品名称（快照）',
    `product_image`  VARCHAR(255)   DEFAULT NULL             COMMENT '商品图片（快照）',
    `price`          DECIMAL(10,2)  NOT NULL                 COMMENT '下单时单价',
    `quantity`       INT            NOT NULL                 COMMENT '购买数量',
    `total_price`    DECIMAL(10,2)  NOT NULL                 COMMENT '小计 = price × quantity',
    `created_at`     DATETIME       NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    PRIMARY KEY (`id`),
    KEY `idx_order_id` (`order_id`),
    KEY `idx_product_id` (`product_id`),
    CONSTRAINT `fk_item_order` FOREIGN KEY (`order_id`) REFERENCES `order`(`id`),
    CONSTRAINT `fk_item_product` FOREIGN KEY (`product_id`) REFERENCES `product`(`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='订单明细表';


-- ==================== 9. 评价表 ====================
DROP TABLE IF EXISTS `review`;
CREATE TABLE `review` (
    `id`            BIGINT       NOT NULL AUTO_INCREMENT  COMMENT '评价ID',
    `user_id`       BIGINT       NOT NULL                 COMMENT '用户ID',
    `product_id`    BIGINT       NOT NULL                 COMMENT '商品ID',
    `order_id`      BIGINT       NOT NULL                 COMMENT '订单ID',
    `rating`        TINYINT      NOT NULL                 COMMENT '评分: 1~5星',
    `content`       VARCHAR(500) DEFAULT NULL             COMMENT '评价内容',
    `images`        VARCHAR(1000) DEFAULT NULL            COMMENT '评价图片，逗号分隔',
    `created_at`    DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updated_at`    DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `deleted`       TINYINT      NOT NULL DEFAULT 0       COMMENT '逻辑删除',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_user_order_product` (`user_id`, `order_id`, `product_id`),
    KEY `idx_product_id` (`product_id`),
    KEY `idx_rating` (`rating`),
    CONSTRAINT `fk_review_user` FOREIGN KEY (`user_id`) REFERENCES `user`(`id`),
    CONSTRAINT `fk_review_product` FOREIGN KEY (`product_id`) REFERENCES `product`(`id`),
    CONSTRAINT `fk_review_order` FOREIGN KEY (`order_id`) REFERENCES `order`(`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='评价表';


-- ==================== 10. 收藏表 ====================
DROP TABLE IF EXISTS `favorite`;
CREATE TABLE `favorite` (
    `id`            BIGINT   NOT NULL AUTO_INCREMENT  COMMENT '收藏ID',
    `user_id`       BIGINT   NOT NULL                 COMMENT '用户ID',
    `product_id`    BIGINT   NOT NULL                 COMMENT '商品ID',
    `created_at`    DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_user_product` (`user_id`, `product_id`),
    CONSTRAINT `fk_favorite_user` FOREIGN KEY (`user_id`) REFERENCES `user`(`id`),
    CONSTRAINT `fk_favorite_product` FOREIGN KEY (`product_id`) REFERENCES `product`(`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='收藏表';


-- ==================== 11. 轮播图表 ====================
DROP TABLE IF EXISTS `banner`;
CREATE TABLE `banner` (
    `id`            BIGINT       NOT NULL AUTO_INCREMENT  COMMENT '轮播图ID',
    `title`         VARCHAR(100) DEFAULT NULL             COMMENT '标题',
    `image_url`     VARCHAR(255) NOT NULL                 COMMENT '图片URL',
    `link_url`      VARCHAR(255) DEFAULT NULL             COMMENT '跳转链接',
    `sort_order`    INT          NOT NULL DEFAULT 0       COMMENT '排序值',
    `status`        TINYINT      NOT NULL DEFAULT 1       COMMENT '状态: 0=禁用, 1=启用',
    `created_at`    DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updated_at`    DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `deleted`       TINYINT      NOT NULL DEFAULT 0       COMMENT '逻辑删除',
    PRIMARY KEY (`id`),
    KEY `idx_status_sort` (`status`, `sort_order`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='轮播图表';
