package com.enshi.springmart.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.io.Serializable;

@Data
public class CategoryUpdateDTO implements Serializable {
    private static final long serialVersionUID = 1L;

    // 分类ID由路径参数 /update/{id} 传入，这里不需要校验，也不该要求前端再传一遍
    private Long id;

    @NotBlank(message = "分类名称不能为空")
    @Size(max = 50, message = "分类名称长度不能超过50个字符")
    private String name;

    @NotNull(message = "父分类ID不能为空")
    private Long parentId;

    private Integer sortOrder;

    @Size(max = 255, message = "图标URL长度不能超过255个字符")
    private String icon;

    @Size(max = 255, message = "分类描述长度不能超过255个字符")
    private String description;
}
