package com.enshi.springmart.vo;

import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 返回给前端看的店铺信息
 */
@Data
public class ShopVO implements Serializable {
    private static final long serialVersionUID = 1L;

    private Long id;

    private Long userId;

    private String name;

    private String logo;

    private String banner;

    private String description;

    private String phone;

    private Integer status;

    private BigDecimal rating;

    private LocalDateTime createdAt;
}
