package com.enshi.springmart.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.enshi.springmart.common.result.PageResult;
import com.enshi.springmart.dto.ProductSaveDTO;
import com.enshi.springmart.entity.Product;
import com.enshi.springmart.vo.ProductVO;

import java.util.List;

public interface ProductService extends IService<Product> {

    /**
     * 按分类和关键词分页查询商品列表
     * @param categoryId 分类ID，传null或0表示全部
     * @param keyword    搜索关键词（模糊匹配商品名），传null或空表示不筛选
     * @param page       页码，从1开始
     * @param pageSize   每页条数
     * @return 分页商品VO
     */
    PageResult<ProductVO> listByCategory(Long categoryId, String keyword, int page, int pageSize);

    /**
     * 查询商品详情（含店铺名）
     * @param id 商品ID
     * @return 商品VO
     */
    ProductVO getProductDetail(Long id);

    /**
     * 添加商品
     * @param productSaveDTO 添加商品参数DTO
     * @return 是否添加成功
     */
    boolean saveProduct(ProductSaveDTO productSaveDTO);

    /**
     * 修改商品
     * @param productSaveDTO 修改商品参数DTO
     * @return 是否修改成功
     */
    boolean updateProduct(ProductSaveDTO productSaveDTO);

    /**
     * 删除商品（逻辑删除）
     * @param id 商品ID
     * @return 是否删除成功
     */
    boolean deleteProduct(Long id);

    /**
     * 变更商品状态（上架 / 下架）
     * @param id 商品ID
     * @param status 状态值（0:下架, 1:上架）
     * @return 是否变更成功
     */
    boolean updateStatus(Long id, Integer status);

    /**
     * 原子扣减库存（下单时调用）
     * 内部用 UPDATE ... WHERE stock >= ? 保证不超卖
     * @param id       商品ID
     * @param quantity 扣减数量
     * @throws BusinessException 库存不足时抛出 STOCK_INSUFFICIENT
     */
    void deductStock(Long id, Integer quantity);

    /**
     * 回滚库存（取消订单 / 支付超时时调用）
     * @param id       商品ID
     * @param quantity 回滚数量
     */
    void restoreStock(Long id, Integer quantity);

}
