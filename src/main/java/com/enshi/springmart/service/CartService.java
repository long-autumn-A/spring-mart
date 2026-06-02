package com.enshi.springmart.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.enshi.springmart.dto.CartSaveDTO;
import com.enshi.springmart.entity.Cart;
import com.enshi.springmart.vo.CartVO;

import java.util.List;

public interface CartService extends IService<Cart> {

    /**
     * 添加商品到购物车 / 合并相同商品数量
     * @param userId      用户ID
     * @param cartSaveDTO 包含商品ID和添加数量
     */
    void addCart(Long userId, CartSaveDTO cartSaveDTO);

    /**
     * 直接修改购物车中商品的数量 (如用户在购物车页面点 +/- 加减号，或者直接输入数字)
     * @param userId   用户ID (用于越权校验)
     * @param cartId   购物车记录ID
     * @param quantity 修改后的最终目标数量
     */
    void updateQuantity(Long userId, Long cartId, Integer quantity);

    /**
     * 批量删除购物车中的商品 (支持单个删除和勾选多个批量删除)
     * @param userId  用户ID
     * @param cartIds 购物车记录ID列表
     */
    void deleteCartItems(Long userId, List<Long> cartIds);

    /**
     * 清空当前用户的购物车
     * @param userId 用户ID
     */
    void clearCart(Long userId);

    /**
     * 获取当前用户的购物车列表 (包含联表查询出来的商品名称、图片、价格、库存及状态)
     * @param userId 用户ID
     * @return 购物车列表
     */
    List<CartVO> getCartList(Long userId);
}
