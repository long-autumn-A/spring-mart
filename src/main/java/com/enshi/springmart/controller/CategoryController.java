package com.enshi.springmart.controller;

import com.enshi.springmart.common.exception.BusinessException;
import com.enshi.springmart.common.result.Result;
import com.enshi.springmart.common.result.ResultCode;
import com.enshi.springmart.dto.CategorySaveDTO;
import com.enshi.springmart.dto.CategoryUpdateDTO;
import com.enshi.springmart.service.CategoryService;
import com.enshi.springmart.utils.UserContext;
import com.enshi.springmart.vo.CategoryTreeVO;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/categories")
public class CategoryController {
    @Autowired
    private CategoryService categoryService;

    // ==================== 公开接口 ====================

    // 获取分类树，所有人都可以看
    @GetMapping("/list")
    public Result<List<CategoryTreeVO>> getCategoryTree() {
        List<CategoryTreeVO> tree = categoryService.getCategoryTree();
        return Result.success(tree);
    }

    // 获取扁平分类列表（首页标签用，不含children）
    @GetMapping("/flat")
    public Result<List<CategoryTreeVO>> getCategoryFlatList() {
        List<CategoryTreeVO> flatList = categoryService.getFlatCategoryList();
        return Result.success(flatList);
    }

    // ==================== 管理员接口 ====================

    // 新增分类
    @PostMapping("/add")
    public Result<?> addCategory(@Valid @RequestBody CategorySaveDTO saveDTO) {
        checkAdmin();
        categoryService.saveCategory(saveDTO);
        return Result.success("分类新增成功", null);
    }

    // 修改分类
    @PutMapping("/update/{id}")
    public Result<?> updateCategory(@PathVariable Long id,
                                     @Valid @RequestBody CategoryUpdateDTO updateDTO) {
        checkAdmin();
        updateDTO.setId(id);
        categoryService.updateCategory(updateDTO);
        return Result.success("分类修改成功", null);
    }

    // 删除分类
    @DeleteMapping("/delete/{id}")
    public Result<?> deleteCategory(@PathVariable Long id) {
        checkAdmin();
        categoryService.deleteCategory(id);
        return Result.success("分类删除成功", null);
    }

    // ==================== 权限校验 ====================

    private void checkAdmin() {
        Integer role = UserContext.getRole();
        if (role == null || role != 2) {
            throw new BusinessException(ResultCode.FORBIDDEN);
        }
    }
}
