package com.enshi.springmart.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.enshi.springmart.common.exception.BusinessException;
import com.enshi.springmart.common.result.ResultCode;
import com.enshi.springmart.dto.CartSaveDTO;
import com.enshi.springmart.entity.Cart;
import com.enshi.springmart.entity.Product;
import com.enshi.springmart.mapper.CartMapper;
import com.enshi.springmart.mapper.ProductMapper;
import com.enshi.springmart.service.CartService;
import com.enshi.springmart.vo.CartVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class CartServiceImpl extends ServiceImpl<CartMapper, Cart> implements CartService {
    @Autowired
    private CartMapper cartMapper;

    @Autowired
    private ProductMapper productMapper;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void addCart(Long userId, CartSaveDTO cartSaveDTO) {
        Long productId = cartSaveDTO.getProductId();
        Integer quantity = cartSaveDTO.getQuantity();

        // 校验商品是否存在、以及是否上架（status=1表示上架）
        Product product = productMapper.selectById(productId);
        if (product == null || product.getStatus() == 0) {
            throw new BusinessException(ResultCode.CART_PRODUCT_OFF_SHELF);
        }

        // 检查用户购物车是否已经存在该商品
        LambdaQueryWrapper<Cart> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Cart::getUserId, userId)
               .eq(Cart::getProductId, productId);
        Cart existCart = cartMapper.selectOne(wrapper);
        if (existCart == null) {
            // 情况一：购物车不存在该商品，直接新增
            if (quantity > product.getStock()) {
                throw new BusinessException(ResultCode.CART_STOCK_INSUFFICIENT);
            }
            Cart cart = new Cart();
            cart.setUserId(userId);
            cart.setProductId(productId);
            cart.setQuantity(quantity);
            cartMapper.insert(cart);
        } else {
            // 情况二：购物车已有该商品 -> 数量累加，并校验累加后的总数是否超库存
            int totalQuantity = existCart.getQuantity() + quantity;
            if (totalQuantity > product.getStock()) {
                throw new BusinessException(ResultCode.CART_STOCK_INSUFFICIENT);
            }
            existCart.setQuantity(totalQuantity);
            cartMapper.updateById(existCart);
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateQuantity(Long userId, Long cartId, Integer quantity) {
        // 用户校验：只能操作自己的购物车
        LambdaQueryWrapper<Cart> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Cart::getId, cartId)
               .eq(Cart::getUserId, userId);
        Cart cart = cartMapper.selectOne(wrapper);
        if (cart == null) {
            throw new BusinessException(ResultCode.CART_ITEM_NOT_FOUND);
        }

        // 商品存在性校验
        Product product = productMapper.selectById(cart.getProductId());
        if (product == null) {
            throw new BusinessException(ResultCode.PRODUCT_NOT_FOUND);
        }
        // 商品上架校验
        if (product.getStatus() == 0) {
            throw new BusinessException(ResultCode.CART_PRODUCT_OFF_SHELF);
        }
        // 库存校验
        if (product.getStock() < quantity) {
            throw new BusinessException(ResultCode.CART_UPDATE_STOCK_INSUFFICIENT);
        }

        // 执行更新
        cart.setQuantity(quantity);
        cartMapper.updateById(cart);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteCartItems(Long userId, List<Long> cartIds) {
//        批量删除
        LambdaQueryWrapper <Cart> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Cart::getUserId, userId)
                .in(Cart::getId, cartIds);
        cartMapper.delete(wrapper);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void clearCart(Long userId) {
//        清空购物车
        LambdaQueryWrapper <Cart> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Cart::getUserId, userId);
        cartMapper.delete(wrapper);
    }

    @Override
    public List<CartVO> getCartList(Long userId) {
        return cartMapper.selectCartListByUserId(userId);
    }
}
