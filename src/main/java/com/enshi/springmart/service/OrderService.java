package com.enshi.springmart.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.enshi.springmart.dto.OrderCreateDTO;
import com.enshi.springmart.entity.Order;
import com.enshi.springmart.vo.OrderDetailVO;
import com.enshi.springmart.vo.OrderVO;

import java.util.List;

public interface OrderService extends IService<Order> {

    /** 从购物车结算，创建订单（待付款状态） */
    OrderVO submitOrder(Long userId, OrderCreateDTO orderCreateDTO);

    /** 我的订单列表，status 为 null 时查全部 */
    List<OrderVO> getMyOrders(Long userId, Integer status);

    /** 订单详情（含明细），只能查自己的订单 */
    OrderDetailVO getOrderDetail(Long userId, Long orderId);

    /** 模拟支付：待付款 -> 待发货 */
    void payOrder(Long userId, Long orderId);

    /** 取消订单：仅待付款状态可取消 */
    void cancelOrder(Long userId, Long orderId);

    /** 确认收货：待收货 -> 已完成 */
    void confirmOrder(Long userId, Long orderId);

    /** 商家查看自己店铺的订单列表（含明细） */
    List<OrderDetailVO> getShopOrders(Long merchantUserId, Integer status);

    /** 商家发货：待发货 -> 待收货 */
    void shipOrder(Long merchantUserId, Long orderId);
}
