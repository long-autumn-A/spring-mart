package com.enshi.springmart.controller;

import com.enshi.springmart.common.result.Result;
import com.enshi.springmart.dto.OrderCreateDTO;
import com.enshi.springmart.service.OrderService;
import com.enshi.springmart.utils.UserContext;
import com.enshi.springmart.vo.OrderDetailVO;
import com.enshi.springmart.vo.OrderVO;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/order")
public class OrderController {
    @Autowired
    private OrderService orderService;

    /**
     * 创建订单（从购物车结算）
     */
    @PostMapping("/submit")
    public Result<OrderVO> submitOrder(@Valid @RequestBody OrderCreateDTO orderCreateDTO) {
        // 从 Token 上下文中获取通过拦截器解析出的当前真实用户ID
        Long userId = UserContext.getUserId();
        return Result.success("下单成功", orderService.submitOrder(userId, orderCreateDTO));
    }

    /**
     * 我的订单列表，可通过 status 参数按状态筛选
     */
    @GetMapping
    public Result<List<OrderVO>> getMyOrders(@RequestParam(required = false) Integer status) {
        Long userId = UserContext.getUserId();
        return Result.success(orderService.getMyOrders(userId, status));
    }

    /**
     * 订单详情（含商品明细）
     */
    @GetMapping("/{orderId}")
    public Result<OrderDetailVO> getOrderDetail(@PathVariable Long orderId) {
        Long userId = UserContext.getUserId();
        return Result.success(orderService.getOrderDetail(userId, orderId));
    }

    /**
     * 模拟支付
     */
    @PutMapping("/{orderId}/pay")
    public Result<?> payOrder(@PathVariable Long orderId) {
        Long userId = UserContext.getUserId();
        orderService.payOrder(userId, orderId);
        return Result.success("支付成功", null);
    }

    /**
     * 取消订单（仅待付款状态）
     */
    @PutMapping("/{orderId}/cancel")
    public Result<?> cancelOrder(@PathVariable Long orderId) {
        Long userId = UserContext.getUserId();
        orderService.cancelOrder(userId, orderId);
        return Result.success("订单已取消", null);
    }

    /**
     * 确认收货（待收货 -> 已完成）
     */
    @PutMapping("/{orderId}/confirm")
    public Result<?> confirmOrder(@PathVariable Long orderId) {
        Long userId = UserContext.getUserId();
        orderService.confirmOrder(userId, orderId);
        return Result.success("确认收货成功", null);
    }

    // ==================== 商家端 ====================

    /**
     * 商家查看自己店铺的订单（含明细）
     */
    @GetMapping("/shop")
    public Result<List<OrderDetailVO>> getShopOrders(@RequestParam(required = false) Integer status) {
        Long userId = UserContext.getUserId();
        return Result.success(orderService.getShopOrders(userId, status));
    }

    /**
     * 商家发货（待发货 -> 待收货）
     */
    @PutMapping("/{orderId}/ship")
    public Result<?> shipOrder(@PathVariable Long orderId) {
        Long userId = UserContext.getUserId();
        orderService.shipOrder(userId, orderId);
        return Result.success("发货成功", null);
    }
}
