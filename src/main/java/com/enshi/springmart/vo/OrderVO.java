package com.enshi.springmart.vo;

import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 订单列表展示（不含明细）
 */
@Data
public class OrderVO implements Serializable {
    private static final long serialVersionUID = 1L;

    /** 订单ID */
    private Long id;

    /** 订单编号 */
    private String orderNo;

    /** 订单总金额 */
    private BigDecimal totalAmount;

    /** 状态: 0=待付款, 1=待发货, 2=待收货, 3=已完成, 4=已取消 */
    private Integer status;

    /** 收货人姓名 */
    private String receiverName;

    /** 收货人手机号 */
    private String receiverPhone;

    /** 下单时间 */
    private LocalDateTime createdAt;

    /** 付款时间 */
    private LocalDateTime payTime;
}
