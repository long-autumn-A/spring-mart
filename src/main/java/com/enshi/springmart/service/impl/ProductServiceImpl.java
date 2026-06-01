package com.enshi.springmart.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.enshi.springmart.entity.Product;
import com.enshi.springmart.mapper.ProductMapper;
import com.enshi.springmart.service.ProductService;
import com.enshi.springmart.vo.ProductVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
public class ProductServiceImpl extends ServiceImpl<ProductMapper, Product> implements ProductService {

    @Override
    public List<ProductVO> listByCategory(Long categoryId) {
        LambdaQueryWrapper<Product> wrapper = new LambdaQueryWrapper<>();
        // 只查上架商品
        wrapper.eq(Product::getStatus, 1);
        // categoryId 为 null 或 0 → 查全部（推荐页）
        if (categoryId != null && categoryId > 0) {
            wrapper.eq(Product::getCategoryId, categoryId);
        }
        wrapper.orderByAsc(Product::getSortOrder)
               .orderByDesc(Product::getSales);

        List<Product> products = baseMapper.selectList(wrapper);
        return products.stream().map(p -> {
            ProductVO vo = new ProductVO();
            BeanUtils.copyProperties(p, vo);
            return vo;
        }).toList();
    }
}
