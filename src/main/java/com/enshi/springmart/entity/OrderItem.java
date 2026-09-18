package com.enshi.springmart.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 订单明细表
 */
@Data
@TableName("order_item")
public class OrderItem implements Serializable {
    private static final long serialVersionUID = 1L;

    @TableId(type = IdType.AUTO)
    private Long id;

    /** 所属订单ID */
    private Long orderId;

    /** 商品ID */
    private Long productId;

    /** 商品名称（下单时快照） */
    private String productName;

    /** 商品图片（下单时快照） */
    private String productImage;

    /** 下单时单价 */
    private BigDecimal price;

    /** 购买数量 */
    private Integer quantity;

    /** 小计 = price × quantity */
    private BigDecimal totalPrice;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdAt;
}
