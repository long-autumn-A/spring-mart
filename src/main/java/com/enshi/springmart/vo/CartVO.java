package com.enshi.springmart.vo;

import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 购物车列表返回 — 除了购物车自身字段，还拼上商品信息供前端展示
 */
@Data
public class CartVO implements Serializable {
    private static final long serialVersionUID = 1L;

    /** 购物车记录ID */
    private Long id;

    /** 用户ID */
    private Long userId;

    /** 商品ID */
    private Long productId;

    /** 数量 */
    private Integer quantity;

    /** 加入时间 */
    private LocalDateTime createdAt;

    // ===== 以下是商品表的冗余展示字段 =====

    /** 商品名称 */
    private String productName;

    /** 商品主图 */
    private String mainImage;

    /** 商品售价 */
    private BigDecimal price;

    /** 商品当前库存 */
    private Integer stock;

    /** 商品状态: 0=下架, 1=上架 */
    private Integer status;
}
