package com.enshi.springmart.vo;

import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 管理端店铺列表里的一行，比 ShopVO 多了店主信息
 */
@Data
public class AdminShopVO implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long id;

    private Long userId;

    /** 店主账号 */
    private String ownerUsername;

    /** 店主账号状态: 0=正常, 1=禁用 */
    private Integer ownerStatus;

    private String name;

    private String logo;

    private String phone;

    private String description;

    /** 0=关闭（含被平台封禁）, 1=营业中 */
    private Integer status;

    private BigDecimal rating;

    private LocalDateTime createdAt;
}
