package com.enshi.springmart.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@TableName("product")
public class Product implements Serializable {
    private static final long serialVersionUID = 1L;

    @TableId(type = IdType.AUTO)
    private Long id;

    /** 所属分类ID */
    private Long categoryId;

    /** 所属店铺ID，0=平台自营 */
    private Long shopId;

    /** 商品名称 */
    private String name;

    /** 商品简述 */
    private String description;

    /** 商品详情（富文本） */
    private String detail;

    /** 售价 */
    private BigDecimal price;

    /** 原价（划线价） */
    private BigDecimal originalPrice;

    /** 库存 */
    private Integer stock;

    /** 累计销量 */
    private Integer sales;

    /** 商品主图URL */
    private String mainImage;

    /** 角标文字（热卖/新品/爆款等） */
    private String badge;

    /** 状态: 0=下架, 1=上架 */
    private Integer status;

    /** 是否推荐: 0=否, 1=是 */
    private Integer isRecommended;

    /** 排序值（越小越靠前） */
    private Integer sortOrder;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdAt;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updatedAt;

    @TableLogic
    private Integer deleted;
}
