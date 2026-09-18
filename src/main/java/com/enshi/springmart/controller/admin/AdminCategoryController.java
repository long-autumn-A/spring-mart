package com.enshi.springmart.controller.admin;

import com.enshi.springmart.common.result.Result;
import com.enshi.springmart.dto.CategorySaveDTO;
import com.enshi.springmart.dto.CategoryUpdateDTO;
import com.enshi.springmart.service.AdminLogService;
import com.enshi.springmart.service.CategoryService;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 管理端 - 分类维护
 * 分类是全局数据，只有管理员能增删改；查询接口在公开的 /api/v1/categories 下
 */
@RestController
@RequestMapping("/api/v1/admin/category")
public class AdminCategoryController {

    @Resource
    private CategoryService categoryService;

    @Resource
    private AdminLogService adminLogService;

    /** 新增分类 */
    @PostMapping("/add")
    public Result<?> addCategory(@Valid @RequestBody CategorySaveDTO saveDTO) {
        categoryService.saveCategory(saveDTO);
        adminLogService.record("新增分类", "CATEGORY", null, "分类名: " + saveDTO.getName());
        return Result.success("分类新增成功", null);
    }

    /** 修改分类 */
    @PutMapping("/update/{id}")
    public Result<?> updateCategory(@PathVariable Long id,
                                    @Valid @RequestBody CategoryUpdateDTO updateDTO) {
        updateDTO.setId(id);
        categoryService.updateCategory(updateDTO);
        adminLogService.record("修改分类", "CATEGORY", id, "分类名: " + updateDTO.getName());
        return Result.success("分类修改成功", null);
    }

    /** 删除分类（有子分类或有关联商品时会被拦下） */
    @DeleteMapping("/delete/{id}")
    public Result<?> deleteCategory(@PathVariable Long id) {
        categoryService.deleteCategory(id);
        adminLogService.record("删除分类", "CATEGORY", id, null);
        return Result.success("分类删除成功", null);
    }
}
