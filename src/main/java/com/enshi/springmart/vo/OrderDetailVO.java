package com.enshi.springmart.vo;

import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 订单详情展示（含明细列表）
 */
@Data
public class OrderDetailVO implements Serializable {
    private static final long serialVersionUID = 1L;

    /** 订单ID */
    private Long id;

    /** 订单编号 */
    private String orderNo;

    /** 买家用户ID */
    private Long userId;

    /** 收货人姓名（快照） */
    private String receiverName;

    /** 收货人手机号（快照） */
    private String receiverPhone;

    /** 收货地址（快照） */
    private String receiverAddress;

    /** 订单总金额 */
    private BigDecimal totalAmount;

    /** 状态: 0=待付款, 1=待发货, 2=待收货, 3=已完成, 4=已取消 */
    private Integer status;

    /** 付款时间 */
    private LocalDateTime payTime;

    /** 发货时间 */
    private LocalDateTime deliverTime;

    /** 完成时间 */
    private LocalDateTime finishTime;

    /** 取消时间 */
    private LocalDateTime cancelTime;

    /** 买家备注 */
    private String remark;

    /** 下单时间 */
    private LocalDateTime createdAt;

    /** 订单明细列表 */
    private List<OrderItemVO> items;
}
