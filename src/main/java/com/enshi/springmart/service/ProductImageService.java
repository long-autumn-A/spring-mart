package com.enshi.springmart.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.enshi.springmart.entity.ProductImage;

import java.util.List;

public interface ProductImageService extends IService<ProductImage> {

    /**
     * 获取某商品的图片列表（按排序值升序）
     */
    List<ProductImage> listByProductId(Long productId);

    /**
     * 为商品添加一张图片
     */
    boolean addImage(Long productId, String imageUrl, Integer sortOrder);

    /**
     * 删除某商品的所有图片
     */
    void deleteByProductId(Long productId);
}
