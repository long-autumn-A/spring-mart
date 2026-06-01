package com.enshi.springmart.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.enshi.springmart.entity.ProductImage;
import com.enshi.springmart.mapper.ProductImageMapper;
import com.enshi.springmart.service.ProductImageService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
public class ProductImageServiceImpl extends ServiceImpl<ProductImageMapper, ProductImage> implements ProductImageService {

    @Override
    public List<ProductImage> listByProductId(Long productId) {
        return this.lambdaQuery()
                .eq(ProductImage::getProductId, productId)
                .orderByAsc(ProductImage::getSortOrder)
                .list();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean addImage(Long productId, String imageUrl, Integer sortOrder) {
        ProductImage image = new ProductImage();
        image.setProductId(productId);
        image.setImageUrl(imageUrl);
        image.setSortOrder(sortOrder != null ? sortOrder : 0);
        int rows = baseMapper.insert(image);
        log.info("商品图片添加: productId={}, imageUrl={}", productId, imageUrl);
        return rows > 0;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteByProductId(Long productId) {
        boolean removed = this.lambdaUpdate()
                .eq(ProductImage::getProductId, productId)
                .remove();
        log.info("商品图片批量删除: productId={}, 已删除={}", productId, removed);
    }
}
