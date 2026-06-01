package com.enshi.springmart.vo;

import lombok.Data;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Data
public class CategoryTreeVO implements Serializable {
    private static final long serialVersionUID = 1L;

    private Long id;

    private String name;

    private Long parentId;

    private Integer sortOrder;

    private String icon;

    private String description;

    /**
     * 子分类列表（核心：实现树形嵌套）
     */
    private List<CategoryTreeVO> children = new ArrayList<>();
}
