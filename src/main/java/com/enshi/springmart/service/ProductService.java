package com.enshi.springmart.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.enshi.springmart.entity.Product;
import com.enshi.springmart.vo.ProductVO;

import java.util.List;

public interface ProductService extends IService<Product> {

    /**
     * 按分类查询商品列表
     * @param categoryId 分类ID，传null或0表示查全部（推荐）
     * @return 商品VO列表
     */
    List<ProductVO> listByCategory(Long categoryId);
}
