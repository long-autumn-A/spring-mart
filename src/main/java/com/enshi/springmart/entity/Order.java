package com.enshi.springmart.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 订单表
 */
@Data
@TableName("`order`")
public class Order implements Serializable {
    private static final long serialVersionUID = 1L;

    @TableId(type = IdType.AUTO)
    private Long id;

    /** 订单编号（雪花ID + 时间戳，全局唯一） */
    private String orderNo;

    /** 买家用户ID */
    private Long userId;

    /** 收货人姓名（下单时快照） */
    private String receiverName;

    /** 收货人手机号（下单时快照） */
    private String receiverPhone;

    /** 收货地址全量拼接（下单时快照） */
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

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdAt;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updatedAt;

    @TableLogic
    private Integer deleted;
}
