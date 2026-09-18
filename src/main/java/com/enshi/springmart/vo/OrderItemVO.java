package com.enshi.springmart.vo;

import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * 订单明细展示
 */
@Data
public class OrderItemVO implements Serializable {
    private static final long serialVersionUID = 1L;

    /** 明细ID */
    private Long id;

    /** 商品ID */
    private Long productId;

    /** 商品名称（快照） */
    private String productName;

    /** 商品图片（快照） */
    private String productImage;

    /** 下单时单价 */
    private BigDecimal price;

    /** 购买数量 */
    private Integer quantity;

    /** 小计 */
    private BigDecimal totalPrice;
}
