package com.enshi.springmart.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.enshi.springmart.dto.CategorySaveDTO;
import com.enshi.springmart.dto.CategoryUpdateDTO;
import com.enshi.springmart.entity.Category;
import com.enshi.springmart.vo.CategoryTreeVO;

import java.util.List;

public interface CategoryService extends IService<Category>{
    /**
     * 新增分类
     * @param saveDTO 新增分类参数
     * @return 是否成功
     */
boolean saveCategory(CategorySaveDTO saveDTO);
    /**
     * 删除分类（含业务校验：有子分类或有关联商品时不允许删除）
     * @param id 分类ID
     * @return 是否成功
     */
    boolean deleteCategory(Long id);

    /**
     * 修改分类
     * @param updateDTO 修改分类参数
     * @return 是否成功
     */
    boolean updateCategory(CategoryUpdateDTO updateDTO);

    /**
     * 获取全量分类树形结构（带缓存优化）
     * @return 树形分类列表
     */
    List<CategoryTreeVO> getCategoryTree();

    /**
     * 获取扁平分类列表（不含children嵌套，首页标签用）
     * @return 扁平分类列表
     */
    List<CategoryTreeVO> getFlatCategoryList();
}
