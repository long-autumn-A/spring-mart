package com.enshi.springmart.controller;

import com.enshi.springmart.common.result.Result;
import com.enshi.springmart.service.CategoryService;
import com.enshi.springmart.vo.CategoryTreeVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

// 分类的查询接口（公开）
// 增删改在管理端 /api/v1/admin/category 下，由 AdminInterceptor 保证只有管理员能操作
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

}
