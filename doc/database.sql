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
    `description`   VARCHAR(255) DEFAULT NULL            COMMENT '分类描述',
    `created_at`    DATETIME    NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updated_at`    DATETIME    NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `deleted`       TINYINT     NOT NULL DEFAULT 0       COMMENT '逻辑删除',
    PRIMARY KEY (`id`),
    KEY `idx_parent_id` (`parent_id`),
    KEY `idx_sort_order` (`sort_order`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='商品分类表';
 ALTER TABLE `category` ADD COLUMN `description` VARCHAR(255) DEFAULT NULL COMMENT '分类描述' AFTER `icon`;

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
    `badge`          VARCHAR(20)    DEFAULT NULL             COMMENT '角标（热卖/新品/爆款等）',
    `status`         TINYINT        NOT NULL DEFAULT 0       COMMENT '状态: 0=下架, 1=上架',
    `is_recommended` TINYINT        NOT NULL DEFAULT 0       COMMENT '是否推荐: 0=否, 1=是',
    `sort_order`     INT            NOT NULL DEFAULT 0       COMMENT '排序值（越小越靠前）',
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

  ALTER TABLE `product`
     ADD COLUMN `badge`     VARCHAR(20) DEFAULT NULL COMMENT '角标（热卖/新品/爆款等）' AFTER `main_image`,
     ADD COLUMN `sort_order` INT         NOT NULL DEFAULT 0   COMMENT '排序值（越小越靠前）' AFTER `is_recommended`;


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


-- =====================================================
-- 种子数据：分类
-- =====================================================
INSERT INTO `category` (`id`, `name`, `parent_id`, `sort_order`, `icon`, `description`) VALUES
(1,  '富硒茗茶', 0, 1, '🍵', '来自恩施高山富硒茶园的天然好茶，传统蒸青工艺，回味甘醇'),
(2,  '土家腊味', 0, 2, '🥩', '土家族百年传承的熏制工艺，松柏枝慢火熏烤，腊香浓郁'),
(3,  '山珍干货', 0, 3, '🍄', '采自武陵山区深处的野生菌菇与珍稀食材，自然晾晒锁鲜'),
(4,  '特色小吃', 0, 4, '🫘', '恩施街头巷尾的地道风味，土家酱香饼、柏杨豆干等经典零嘴'),
(5,  '高山杂粮', 0, 5, '🌽', '恩施富硒土壤孕育的土豆、大米、豆皮，硒含量世界罕见'),
(6,  '山野调味', 0, 6, '🌶️', '土家厨房的秘密武器，鲊广椒、油茶汤、酸萝卜，一勺入魂'),
(7,  '农家蜜果', 0, 7, '🍯', '深山蜂农自产土蜂蜜，高山果园直供猕猴桃、关口葡萄');

-- =====================================================
-- 种子数据：商品
-- =====================================================
INSERT INTO `product` (`id`, `name`, `category_id`, `description`, `price`, `sales`, `main_image`, `badge`, `status`, `is_recommended`, `sort_order`) VALUES
(101, '恩施玉露·特级',       1, '蒸青绿茶，一芽一叶，富硒认证',            168.00, 2380, '', '热卖', 1, 1, 1),
(102, '利川红茶·工夫红',     1, '利川高山红茶，蜜香醇厚',                  128.00, 1520, '', '',     1, 1, 2),
(103, '来凤藤茶·野生',       1, '武陵山区野生藤茶，回甘绵长',               89.00,  980,  '', '新品', 1, 1, 3),
(104, '宣恩贡茶·明前',       1, '明清贡品，栗香馥郁',                      198.00,  650, '', '',     1, 1, 4),
(105, '花枝茶·高山云雾',     1, '云雾滋养，花香清雅',                       78.00, 1120, '', '',     1, 0, 5),
(201, '土家腊肉·五花',       2, '松柏慢熏45天，肥瘦相间',                  68.00,  3200, '', '爆款', 1, 1, 1),
(202, '土家腊蹄子',           2, '整只猪蹄熏制，胶原满满',                  88.00,  1890, '', '',     1, 1, 2),
(203, '宣恩火腿·精腿',       2, '宣恩传统工艺，三年陈腿',                  268.00,  420, '', '',     1, 1, 3),
(204, '土家腊排骨',           2, '精选肋排，骨香肉嫩',                      58.00,  2100, '', '热卖', 1, 1, 4),
(205, '年肉·土家风味',       2, '年节必备，肥而不腻',                      45.00,  1760, '', '',     1, 0, 5),
(301, '葛仙米·野生',         3, '纯野生水藻，晶莹剔透',                    158.00,  760, '', '珍品', 1, 1, 1),
(302, '福宝山莼菜',           3, '高山冷水莼菜，嫩滑爽口',                  38.00,  1450, '', '',     1, 1, 2),
(303, '凤头姜·新鲜',         3, '形似凤头，脆嫩香辣',                      25.00,  2800, '', '热卖', 1, 1, 3),
(304, '景阳核桃·薄壳',       3, '皮薄仁满，自然原香',                      42.00,  1680, '', '',     1, 0, 4),
(305, '板桥党参·特级',       3, '中国板党之乡，补气佳品',                  198.00,  530, '', '',     1, 1, 5),
(401, '柏杨豆干·经典',       4, '土家一绝，薄如纸片，嚼劲十足',            22.00,  5600, '', '爆款', 1, 1, 1),
(402, '巴东五香豆干',         4, '五香卤制，回味无穷',                      18.00,  4100, '', '',     1, 1, 2),
(403, '土家酱香饼·即食',     4, '中国披萨，酱香浓郁',                      28.00,  3800, '', '热卖', 1, 1, 3),
(404, '恩施土家糍粑',         4, '糯米手工捶打，软糯香甜',                  35.00,  2200, '', '',     1, 1, 4),
(405, '花坪桃片糕',           4, '传统手工糕点，细腻绵软',                  16.00,  3400, '', '',     1, 0, 5),
(406, '土家烧饼·五香',       4, '炭火烤制，外酥里嫩',                      15.00,  5000, '', '',     1, 1, 6),
(501, '恩施富硒土豆·5斤',    5, '世界硒都，软糯香甜，含硒认证',            29.00,  8900, '', '爆款', 1, 1, 1),
(502, '景阳大米·富硒',       5, '中国最贵大米之一，形似玉色',              128.00,  320, '', '珍品', 1, 1, 2),
(503, '恩施豆皮·米豆皮',     5, '纯米浆制，软糯有嚼劲',                    12.00,  6300, '', '',     1, 1, 3),
(504, '野三关苞谷酒',         5, '玉米纯酿，土家茅台',                      58.00,  1900, '', '',     1, 0, 4),
(505, '社饭料包·传统',       5, '野葱腊肉糯米，一包搞定',                  20.00,  2500, '', '',     1, 1, 5),
(601, '鲊广椒·土家风味',     6, '鲜红辣椒配苞谷面，酸辣开胃',              18.00,  4200, '', '热卖', 1, 1, 1),
(602, '恩施酸萝卜',           6, '老坛泡制，酸甜脆爽',                      15.00,  3500, '', '',     1, 1, 2),
(603, '建南咸菜·老坛',       6, '传统工艺腌制，佐饭神器',                  12.00,  2800, '', '',     1, 0, 3),
(604, '土家油茶汤料',         6, '武陵山区传统茶汤，一冲即饮',              32.00,  1600, '', '',     1, 1, 4),
(605, '恩施水豆豉',           6, '黄豆发酵，鲜香微辣',                      10.00,  4100, '', '',     1, 1, 5),
(701, '尚风寨土蜂蜜·纯',     7, '深山蜂农自采，零添加纯天然',              98.00,  1800, '', '',     1, 1, 1),
(702, '关口葡萄·新鲜',       7, '高山葡萄，甜度高汁水足',                  36.00,  2100, '', '时令', 1, 1, 2),
(703, '建始猕猴桃·绿心',     7, '富硒土壤培育，维C爆表',                   28.00,  3200, '', '时令', 1, 1, 3),
(704, '恩施黑猪肉·生鲜',     7, '散养黑猪，肉质紧实鲜嫩',                  58.00,   950, '', '',     1, 0, 4);

-- =====================================================
-- 种子数据：店铺（需先注册商家用户后替换 user_id）
-- =====================================================
-- 注册示例：POST /api/v1/user/register  {"username":"zhangsan","password":"123456","nickname":"张三土家腊味铺","phone":"13800000001","role":1}
-- 注册示例：POST /api/v1/user/register  {"username":"lisi","password":"123456","nickname":"李四高山茶庄","phone":"13800000002","role":1}
-- 然后用返回的 userId 替换下面 INSERT 中的 user_id
-- INSERT INTO `shop` (`user_id`, `name`, `description`, `phone`, `status`) VALUES
-- (2, '张三土家腊味铺', '三代传承的土家腊味老店，纯手工熏制，地道恩施味', '13800000001', 1),
-- (3, '李四高山茶庄', '自家恩施高山茶园，富硒认证，从枝头到杯中的新鲜', '13800000002', 1);

