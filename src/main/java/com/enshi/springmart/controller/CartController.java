package com.enshi.springmart.controller;

import com.enshi.springmart.common.result.Result;
import com.enshi.springmart.dto.CartSaveDTO;
import com.enshi.springmart.service.CartService;
import com.enshi.springmart.utils.UserContext;
import com.enshi.springmart.vo.CartVO;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/cart")
public class CartController {

    @Autowired
    private CartService cartService;

    /**
     * 添加商品到购物车
     */
    @PostMapping
    public Result<?> addCart(@Valid @RequestBody CartSaveDTO cartSaveDTO) {
        Long userId = UserContext.getUserId();
        cartService.addCart(userId, cartSaveDTO);
        return Result.success("已加入购物车", null);
    }

    /**
     * 获取当前用户的购物车列表
     */
    @GetMapping
    public Result<List<CartVO>> getCartList() {
        Long userId = UserContext.getUserId();
        List<CartVO> list = cartService.getCartList(userId);
        return Result.success(list);
    }

    /**
     * 修改购物车中某商品的数量
     */
    @PutMapping("/{cartId}")
    public Result<?> updateQuantity(@PathVariable Long cartId,
                                    @RequestParam Integer quantity) {
        Long userId = UserContext.getUserId();
        cartService.updateQuantity(userId, cartId, quantity);
        return Result.success("数量已更新", null);
    }

    /**
     * 批量删除购物车商品（支持单个和批量）
     */
    @DeleteMapping
    public Result<?> deleteCartItems(@RequestBody List<Long> cartIds) {
        Long userId = UserContext.getUserId();
        cartService.deleteCartItems(userId, cartIds);
        return Result.success("已删除", null);
    }

    /**
     * 清空当前用户的购物车
     */
    @DeleteMapping("/clear")
    public Result<?> clearCart() {
        Long userId = UserContext.getUserId();
        cartService.clearCart(userId);
        return Result.success("购物车已清空", null);
    }
}
