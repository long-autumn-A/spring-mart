package com.enshi.springmart.controller;

import com.enshi.springmart.common.exception.BusinessException;
import com.enshi.springmart.common.result.PageResult;
import com.enshi.springmart.common.result.Result;
import com.enshi.springmart.common.result.ResultCode;
import com.enshi.springmart.dto.ProductSaveDTO;
import com.enshi.springmart.entity.ProductImage;
import com.enshi.springmart.service.ProductImageService;
import com.enshi.springmart.service.ProductService;
import com.enshi.springmart.vo.ProductVO;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/goods")
public class ProductController {

    @Autowired
    private ProductService productService;

    @Autowired
    private ProductImageService productImageService;

    // ==================== 公开接口 ====================

    /**
     * 按分类 + 关键词分页获取商品列表（支持组合查询）
     * @param categoryId 分类ID，0或不传表示全部
     * @param keyword    搜索关键词（模糊匹配商品名），不传则查全部
     * @param page       页码，默认第1页
     * @param pageSize   每页条数，默认10条
     */
    @GetMapping("/list")
    public Result<PageResult<ProductVO>> list(@RequestParam(required = false, defaultValue = "0") Long categoryId,
                                               @RequestParam(required = false) String keyword,
                                               @RequestParam(required = false, defaultValue = "1") int page,
                                               @RequestParam(required = false, defaultValue = "10") int pageSize) {
        PageResult<ProductVO> pageResult = productService.listByCategory(categoryId, keyword, page, pageSize);
        return Result.success(pageResult);
    }

    /**
     * 商品详情
     */
    @GetMapping("/detail/{id}")
    public Result<ProductVO> detail(@PathVariable Long id) {
        ProductVO vo = productService.getProductDetail(id);
        return Result.success(vo);
    }

    // ==================== 管理员和商家接口 ====================

    /**
     * 新增商品
     */
    @PostMapping
    public Result<?> addProduct(@Valid @RequestBody ProductSaveDTO dto, HttpServletRequest request) {
        checkAdminOrMerchant(request);
        productService.saveProduct(dto);
        return Result.success("商品新增成功", null);
    }

    /**
     * 修改商品
     */
    @PutMapping("/{id}")
    public Result<?> updateProduct(@PathVariable Long id,
                                   @Valid @RequestBody ProductSaveDTO dto,
                                   HttpServletRequest request) {
        checkAdminOrMerchant(request);
        dto.setId(id);
        productService.updateProduct(dto);
        return Result.success("商品修改成功", null);
    }

    /**
     * 删除商品（逻辑删除）
     */
    @DeleteMapping("/{id}")
    public Result<?> deleteProduct(@PathVariable Long id, HttpServletRequest request) {
        checkAdminOrMerchant(request);
        productService.deleteProduct(id);
        return Result.success("商品删除成功", null);
    }

    /**
     * 变更商品状态（上架/下架）
     */
    @PutMapping("/{id}/status")
    public Result<?> updateStatus(@PathVariable Long id,
                                  @RequestParam Integer status,
                                  HttpServletRequest request) {
        checkAdminOrMerchant(request);
        productService.updateStatus(id, status);
        return Result.success("商品状态变更成功", null);
    }

    // ==================== 商品轮播图管理 ====================

    /**
     * 获取某商品的轮播图列表
     */
    @GetMapping("/{productId}/images")
    public Result<List<ProductImage>> listImages(@PathVariable Long productId) {
        List<ProductImage> images = productImageService.listByProductId(productId);
        return Result.success(images);
    }

    /**
     * 为商品添加轮播图
     */
    @PostMapping("/{productId}/images")
    public Result<?> addImage(@PathVariable Long productId,
                              @RequestParam String imageUrl,
                              @RequestParam(required = false, defaultValue = "0") Integer sortOrder,
                              HttpServletRequest request) {
        checkAdminOrMerchant(request);
        productImageService.addImage(productId, imageUrl, sortOrder);
        return Result.success("图片添加成功", null);
    }

    /**
     * 删除单张轮播图
     */
    @DeleteMapping("/images/{imageId}")
    public Result<?> deleteImage(@PathVariable Long imageId, HttpServletRequest request) {
        checkAdminOrMerchant(request);
        productImageService.removeById(imageId);
        return Result.success("图片删除成功", null);
    }

    // ==================== 权限校验 ====================

    /**
     * 校验管理员或商家身份（管理员 role=2 或商家 role=1）
     */
    private void checkAdminOrMerchant(HttpServletRequest request) {
        Object roleObj = request.getAttribute("currentUserRole");
        if (roleObj == null) {
            throw new BusinessException(ResultCode.UNAUTHORIZED);
        }
        String role = roleObj.toString();
        // 管理员(2) 或 商家(1) 都可以操作商品
        if (!"1".equals(role) && !"2".equals(role)) {
            throw new BusinessException(ResultCode.FORBIDDEN);
        }
    }

    /**
     * 严格校验管理员身份（role=2），用于敏感操作
     */
    private void checkAdmin(HttpServletRequest request) {
        Object roleObj = request.getAttribute("currentUserRole");
        if (roleObj == null || !"2".equals(roleObj.toString())) {
            throw new BusinessException(ResultCode.FORBIDDEN);
        }
    }
}
