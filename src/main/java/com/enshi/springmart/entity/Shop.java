package com.enshi.springmart.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 店铺表，一个商家只能开一家店
 */
@Data
@TableName("shop")
public class Shop implements Serializable {
    private static final long serialVersionUID = 1L;

    @TableId(type = IdType.AUTO)
    private Long id;

    /** 商家用户ID，一个用户只能有一家店 */
    private Long userId;

    /** 店铺名称 */
    private String name;

    /** 店铺Logo URL */
    private String logo;

    /** 店铺横幅URL */
    private String banner;

    /** 店铺简介 */
    private String description;

    /** 店铺联系电话 */
    private String phone;

    /** 状态: 0=关闭, 1=营业中 */
    private Integer status;

    /** 店铺评分 (1.0~5.0) */
    private BigDecimal rating;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdAt;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updatedAt;

    @TableLogic
    private Integer deleted;
}
