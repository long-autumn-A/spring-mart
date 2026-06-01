package com.enshi.springmart.vo;

import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

@Data
public class ProductVO implements Serializable {
    private static final long serialVersionUID = 1L;

    private Long id;
    private Long categoryId;
    private Long shopId;
    private String shopName;
    private String name;
    private String description;
    private String detail;
    private BigDecimal price;
    private BigDecimal originalPrice;
    private Integer stock;
    private Integer sales;
    private String mainImage;
    private String badge;
    private Integer status;
    private Integer isRecommended;
    private Integer sortOrder;

    /** 商品轮播图列表（来自 product_image 表，暂未实现） */
    private List<String> detailImages;
}
