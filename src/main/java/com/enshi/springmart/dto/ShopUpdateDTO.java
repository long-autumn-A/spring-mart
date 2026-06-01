package com.enshi.springmart.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.io.Serializable;

/**
 * 修改店铺信息时前端传过来的数据
 */
@Data
public class ShopUpdateDTO implements Serializable {
    private static final long serialVersionUID = 1L;

    @NotNull(message = "店铺ID不能为空")
    private Long id;

    @Size(max = 100, message = "店铺名称长度不能超过100个字符")
    private String name;

    @Size(max = 255, message = "Logo URL长度不能超过255个字符")
    private String logo;

    @Size(max = 255, message = "横幅URL长度不能超过255个字符")
    private String banner;

    @Size(max = 500, message = "店铺简介长度不能超过500个字符")
    private String description;

    @Pattern(regexp = "^1[3-9]\\d{9}$", message = "手机号格式不正确")
    private String phone;

    /** 商家自己可以开关店: 0=关闭, 1=营业中 */
    private Integer status;
}
