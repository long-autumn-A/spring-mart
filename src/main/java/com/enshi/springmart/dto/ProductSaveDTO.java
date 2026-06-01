package com.enshi.springmart.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

@Data
public class ProductSaveDTO implements Serializable {
    private static final long serialVersionUID = 1L;

    /** 更新时传，新增时不传 */
    private Long id;

    @NotNull(message = "店铺ID不能为空")
    private Long shopId;

    @NotNull(message = "商品名称不能为空")
    private String name;

    @NotNull(message = "分类ID不能为空")
    private Long categoryId;

    @NotNull(message = "商品价格不能为空")
    @DecimalMin(value = "0.01", message = "价格必须大于0")
    private BigDecimal price;

    /** 划线原价，可不传 */
    private BigDecimal originalPrice;

    @NotNull(message = "商品库存不能为空")
    @Min(value = 0, message = "库存不能为负数")
    private Integer stock;

    /** 商品简述 */
    private String description;

    /** 商品详情（富文本） */
    private String detail;

    /** 商品主图URL */
    private String mainImage;

    /** 角标文字（热卖/新品/爆款等） */
    private String badge;

    /** 是否推荐: 0=否, 1=是 */
    private Integer isRecommended;

    /** 排序值（越小越靠前） */
    private Integer sortOrder;
}
