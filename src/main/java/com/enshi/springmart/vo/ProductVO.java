package com.enshi.springmart.vo;

import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

@Data
public class ProductVO implements Serializable {
    private static final long serialVersionUID = 1L;

    private Long id;
    private Long categoryId;
    private String name;
    private String description;
    private BigDecimal price;
    private BigDecimal originalPrice;
    private Integer sales;
    private String mainImage;
    private String badge;
    private Integer isRecommended;
    private Integer sortOrder;
}
