package com.enshi.springmart.controller;

import com.enshi.springmart.common.result.Result;
import com.enshi.springmart.service.ProductService;
import com.enshi.springmart.vo.ProductVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/goods")
public class ProductController {

    @Autowired
    private ProductService productService;

    /**
     * 公开接口：按分类获取商品列表
     * @param categoryId 分类ID，0或不传表示全部（推荐）
     */
    @GetMapping("/list")
    public Result<List<ProductVO>> list(@RequestParam(required = false, defaultValue = "0") Long categoryId) {
        List<ProductVO> list = productService.listByCategory(categoryId);
        return Result.success(list);
    }
}
