package com.enshi.springmart.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.enshi.springmart.common.exception.BusinessException;
import com.enshi.springmart.common.result.PageResult;
import com.enshi.springmart.common.result.ResultCode;
import com.enshi.springmart.dto.ProductSaveDTO;
import com.enshi.springmart.entity.Category;
import com.enshi.springmart.entity.Product;
import com.enshi.springmart.entity.ProductImage;
import com.enshi.springmart.entity.Shop;
import com.enshi.springmart.mapper.CategoryMapper;
import com.enshi.springmart.mapper.ProductMapper;
import com.enshi.springmart.mapper.ShopMapper;
import com.enshi.springmart.service.ProductImageService;
import com.enshi.springmart.service.ProductService;
import com.enshi.springmart.vo.ProductVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
public class ProductServiceImpl extends ServiceImpl<ProductMapper, Product> implements ProductService {

    @Autowired
    private CategoryMapper categoryMapper;

    @Autowired
    private ShopMapper shopMapper;

    @Autowired
    private ProductImageService productImageService;

    // ==================== 查询 ====================

    @Override
    public PageResult<ProductVO> listByCategory(Long categoryId, String keyword, int page, int pageSize, boolean includeOffline) {
        LambdaQueryWrapper<Product> wrapper = new LambdaQueryWrapper<>();
        // 默认只查上架商品，商家管理后台可以查全部
        if (!includeOffline) {
            wrapper.eq(Product::getStatus, 1);
        }
        // categoryId 为 null 或 0 → 查全部
        if (categoryId != null && categoryId > 0) {
            wrapper.eq(Product::getCategoryId, categoryId);
        }
        // 关键词模糊搜索商品名
        if (keyword != null && !keyword.isBlank()) {
            wrapper.like(Product::getName, keyword);
        }
        wrapper.orderByAsc(Product::getSortOrder)
               .orderByDesc(Product::getSales);

        Page<Product> pageParam = new Page<>(page, pageSize);
        Page<Product> productPage = baseMapper.selectPage(pageParam, wrapper);

        List<ProductVO> voList = productPage.getRecords().stream()
                .map(this::toVO)
                .toList();

        return new PageResult<>(voList, productPage.getTotal(), page, pageSize);
    }

    @Override
    public ProductVO getProductDetail(Long id) {
        Product product = baseMapper.selectById(id);
        if (product == null) {
            throw new BusinessException(ResultCode.PRODUCT_NOT_FOUND);
        }
        ProductVO vo = toVO(product);
        // 查店铺名
        if (product.getShopId() != null && product.getShopId() > 0) {
            Shop shop = shopMapper.selectById(product.getShopId());
            if (shop != null) {
                vo.setShopName(shop.getName());
            }
        }
        // 查轮播图
        List<ProductImage> images = productImageService.listByProductId(id);
        vo.setDetailImages(images.stream()
                .map(ProductImage::getImageUrl)
                .toList());
        return vo;
    }

    /**
     * Product 实体 → ProductVO（列表和详情复用）
     */
    private ProductVO toVO(Product p) {
        ProductVO vo = new ProductVO();
        BeanUtils.copyProperties(p, vo);
        return vo;
    }

    // ==================== 增删改 ====================

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean saveProduct(ProductSaveDTO dto) {
        log.info("saveProduct 开始执行: name={}, shopId={}, categoryId={}, price={}", 
                dto.getName(), dto.getShopId(), dto.getCategoryId(), dto.getPrice());
        // 校验分类是否存在
        Category category = categoryMapper.selectById(dto.getCategoryId());
        if (category == null) {
            throw new BusinessException(ResultCode.CATEGORY_NOT_FOUND);
        }
        // 如果指定了店铺（非平台自营），校验店铺是否存在
        if (dto.getShopId() != null && dto.getShopId() > 0) {
            Shop shop = shopMapper.selectById(dto.getShopId());
            if (shop == null) {
                throw new BusinessException(ResultCode.SHOP_NOT_FOUND);
            }
        }
        Product product = new Product();
        BeanUtils.copyProperties(dto, product);
        // 新增的商品默认下架状态，需要管理员/商家手动上架
        product.setStatus(0);
        product.setSales(0);

        int rows = baseMapper.insert(product);
        log.info("商品新增成功: name={}, id={}, shopId={}", product.getName(), product.getId(), product.getShopId());
        return rows > 0;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean updateProduct(ProductSaveDTO dto) {
        // 校验商品是否存在
        Product product = baseMapper.selectById(dto.getId());
        if (product == null) {
            throw new BusinessException(ResultCode.PRODUCT_NOT_FOUND);
        }
        // 如果改了分类，校验新分类是否存在
        if (dto.getCategoryId() != null) {
            Category category = categoryMapper.selectById(dto.getCategoryId());
            if (category == null) {
                throw new BusinessException(ResultCode.CATEGORY_NOT_FOUND);
            }
        }
        // 如果改了店铺，校验新店铺是否存在
        if (dto.getShopId() != null && dto.getShopId() > 0) {
            Shop shop = shopMapper.selectById(dto.getShopId());
            if (shop == null) {
                throw new BusinessException(ResultCode.SHOP_NOT_FOUND);
            }
        }
        BeanUtils.copyProperties(dto, product);
        int rows = baseMapper.updateById(product);
        log.info("商品更新成功: id={}, name={}", product.getId(), product.getName());
        return rows > 0;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean deleteProduct(Long id) {
        Product product = baseMapper.selectById(id);
        if (product == null) {
            throw new BusinessException(ResultCode.PRODUCT_NOT_FOUND);
        }
        // MyBatis-Plus 的 @TableLogic 会自动把 deleteById 变成逻辑删除
        int rows = baseMapper.deleteById(id);
        // 同时删掉该商品的所有轮播图
        productImageService.deleteByProductId(id);
        log.info("商品删除成功: id={}, name={}", id, product.getName());
        return rows > 0;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean updateStatus(Long id, Integer status) {
        Product product = baseMapper.selectById(id);
        if (product == null) {
            throw new BusinessException(ResultCode.PRODUCT_NOT_FOUND);
        }
        product.setStatus(status);
        int rows = baseMapper.updateById(product);
        log.info("商品状态变更: id={}, name={}, status={}", id, product.getName(), status);
        return rows > 0;
    }

    // ==================== 库存原子操作 ====================

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deductStock(Long id, Integer quantity) {
        // UPDATE product SET stock = stock - ? WHERE id = ? AND stock >= ?
        boolean success = this.lambdaUpdate()
                .setSql("stock = stock - " + quantity)
                .eq(Product::getId, id)
                .ge(Product::getStock, quantity)
                .update();
        if (!success) {
            throw new BusinessException(ResultCode.STOCK_INSUFFICIENT);
        }
        log.info("库存扣减成功: productId={}, quantity={}", id, quantity);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void restoreStock(Long id, Integer quantity) {
        // UPDATE product SET stock = stock + ? WHERE id = ?
        this.lambdaUpdate()
                .setSql("stock = stock + " + quantity)
                .eq(Product::getId, id)
                .update();
        log.info("库存回滚成功: productId={}, quantity={}", id, quantity);
    }
}
