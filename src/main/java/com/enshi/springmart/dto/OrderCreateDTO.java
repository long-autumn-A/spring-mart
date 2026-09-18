package com.enshi.springmart.dto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * 下单请求
 */
@Data
public class OrderCreateDTO implements Serializable {
    private static final long serialVersionUID = 1L;

    /** 收货地址ID */
    @NotNull(message = "收货地址不能为空")
    private Long addressId;

    /** 要结算的购物车项ID列表 */
    @NotEmpty(message = "结算商品不能为空")
    private List<Long> cartItemIds;

    /** 买家备注（可选） */
    private String remark;
}
