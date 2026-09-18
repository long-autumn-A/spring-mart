package com.enshi.springmart.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.IdWorker;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.enshi.springmart.common.exception.BusinessException;
import com.enshi.springmart.common.result.ResultCode;
import com.enshi.springmart.dto.OrderCreateDTO;
import com.enshi.springmart.entity.*;
import com.enshi.springmart.mapper.OrderMapper;
import com.enshi.springmart.service.*;
import com.enshi.springmart.vo.OrderDetailVO;
import com.enshi.springmart.vo.OrderItemVO;
import com.enshi.springmart.vo.OrderVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class OrderServiceImpl extends ServiceImpl<OrderMapper, Order> implements OrderService {

    /** 订单状态常量，与 entity.Order 注释保持一致 */
    private static final int STATUS_UNPAID = 0;
    private static final int STATUS_UNSHIPPED = 1;
    private static final int STATUS_SHIPPED = 2;
    private static final int STATUS_FINISHED = 3;
    private static final int STATUS_CANCELLED = 4;

    @Autowired
    private AddressService addressService;

    @Autowired
    private OrderItemService orderItemService;

    @Autowired
    private CartService cartService;

    @Autowired
    private ProductService productService;

    @Autowired
    private ShopService shopService;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public OrderVO submitOrder(Long userId, OrderCreateDTO orderCreateDTO) {
        // 1. 核验收货地址（必须属于当前用户）
        Address address = addressService.getOne(new LambdaQueryWrapper<Address>()
                .eq(Address::getId, orderCreateDTO.getAddressId())
                .eq(Address::getUserId, userId));
        if (address == null) {
            throw new BusinessException(ResultCode.ORDER_ADDRESS_NOT_FOUND);
        }

        // 2. 查出选中的购物车项（只能结算自己的）
        List<Cart> carts = cartService.list(new LambdaQueryWrapper<Cart>()
                .eq(Cart::getUserId, userId)
                .in(Cart::getId, orderCreateDTO.getCartItemIds()));
        if (carts.isEmpty()) {
            throw new BusinessException(ResultCode.CART_EMPTY_CHECKOUT);
        }

        // 3. 加载商品并校验上架、库存，同时生成订单明细快照
        List<Long> productIds = carts.stream().map(Cart::getProductId).distinct().toList();
        Map<Long, Product> productMap = productService.listByIds(productIds).stream()
                .collect(Collectors.toMap(Product::getId, p -> p));

        List<OrderItem> items = new ArrayList<>();
        BigDecimal totalAmount = BigDecimal.ZERO;
        for (Cart cart : carts) {
            Product product = productMap.get(cart.getProductId());
            if (product == null || product.getStatus() == 0) {
                throw new BusinessException(ResultCode.CART_PRODUCT_OFF_SHELF);
            }
            if (product.getStock() < cart.getQuantity()) {
                throw new BusinessException(ResultCode.ORDER_STOCK_INSUFFICIENT);
            }
            OrderItem item = new OrderItem();
            item.setProductId(product.getId());
            item.setProductName(product.getName());
            item.setProductImage(product.getMainImage());
            item.setPrice(product.getPrice());
            item.setQuantity(cart.getQuantity());
            item.setTotalPrice(product.getPrice().multiply(BigDecimal.valueOf(cart.getQuantity())));
            items.add(item);
            totalAmount = totalAmount.add(item.getTotalPrice());
        }

        // 4. 生成订单主记录（收货信息做快照，防止后续改地址影响历史订单）
        Order order = new Order();
        order.setOrderNo(IdWorker.getIdStr());
        order.setUserId(userId);
        order.setReceiverName(address.getReceiverName());
        order.setReceiverPhone(address.getReceiverPhone());
        order.setReceiverAddress(String.join(" ",
                address.getProvince(), address.getCity(), address.getDistrict(), address.getDetail()));
        order.setTotalAmount(totalAmount);
        order.setStatus(STATUS_UNPAID);
        order.setRemark(orderCreateDTO.getRemark());
        this.save(order);

        // 5. 批量保存订单明细
        items.forEach(item -> item.setOrderId(order.getId()));
        orderItemService.saveBatch(items);

        // 6. 扣减库存并累加销量（SQL 原子操作，防止并发超卖）
        for (Cart cart : carts) {
            productService.update(new LambdaUpdateWrapper<Product>()
                    .eq(Product::getId, cart.getProductId())
                    .setSql("stock = stock - " + cart.getQuantity())
                    .setSql("sales = sales + " + cart.getQuantity()));
        }

        // 7. 移除已结算的购物车项
        cartService.removeByIds(carts.stream().map(Cart::getId).toList());

        return toOrderVO(order);
    }

    @Override
    public List<OrderVO> getMyOrders(Long userId, Integer status) {
        LambdaQueryWrapper<Order> wrapper = new LambdaQueryWrapper<Order>()
                .eq(Order::getUserId, userId)
                .orderByDesc(Order::getCreatedAt);
        if (status != null) {
            wrapper.eq(Order::getStatus, status);
        }
        return this.list(wrapper).stream().map(this::toOrderVO).toList();
    }

    @Override
    public OrderDetailVO getOrderDetail(Long userId, Long orderId) {
        Order order = getOwnedOrder(userId, orderId);
        return toOrderDetailVO(order);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void payOrder(Long userId, Long orderId) {
        Order order = getOwnedOrder(userId, orderId);
        if (order.getStatus() != STATUS_UNPAID) {
            throw new BusinessException(ResultCode.ORDER_STATUS_ERROR);
        }
        order.setStatus(STATUS_UNSHIPPED);
        order.setPayTime(java.time.LocalDateTime.now());
        this.updateById(order);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void cancelOrder(Long userId, Long orderId) {
        Order order = getOwnedOrder(userId, orderId);
        if (order.getStatus() != STATUS_UNPAID) {
            throw new BusinessException(ResultCode.ORDER_CANNOT_CANCEL);
        }
        order.setStatus(STATUS_CANCELLED);
        order.setCancelTime(java.time.LocalDateTime.now());
        this.updateById(order);

        // 取消后把扣掉的库存加回来
        List<OrderItem> items = orderItemService.list(new LambdaQueryWrapper<OrderItem>()
                .eq(OrderItem::getOrderId, orderId));
        for (OrderItem item : items) {
            productService.update(new LambdaUpdateWrapper<Product>()
                    .eq(Product::getId, item.getProductId())
                    .setSql("stock = stock + " + item.getQuantity())
                    .setSql("sales = sales - " + item.getQuantity()));
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void confirmOrder(Long userId, Long orderId) {
        Order order = getOwnedOrder(userId, orderId);
        if (order.getStatus() != STATUS_SHIPPED) {
            throw new BusinessException(ResultCode.ORDER_STATUS_ERROR);
        }
        order.setStatus(STATUS_FINISHED);
        order.setFinishTime(java.time.LocalDateTime.now());
        this.updateById(order);
    }

    @Override
    public List<OrderDetailVO> getShopOrders(Long merchantUserId, Integer status) {
        // 找到商家的店铺，再通过 商品 -> 订单明细 -> 订单 关联查出店铺订单
        Shop shop = shopService.getOne(new LambdaQueryWrapper<Shop>()
                .eq(Shop::getUserId, merchantUserId));
        if (shop == null) {
            throw new BusinessException(ResultCode.SHOP_NOT_FOUND);
        }

        List<Long> productIds = productService.list(new LambdaQueryWrapper<Product>()
                        .eq(Product::getShopId, shop.getId()))
                .stream().map(Product::getId).toList();
        if (productIds.isEmpty()) {
            return List.of();
        }

        Set<Long> orderIds = orderItemService.list(new LambdaQueryWrapper<OrderItem>()
                        .in(OrderItem::getProductId, productIds))
                .stream().map(OrderItem::getOrderId).collect(Collectors.toSet());
        if (orderIds.isEmpty()) {
            return List.of();
        }

        LambdaQueryWrapper<Order> wrapper = new LambdaQueryWrapper<Order>()
                .in(Order::getId, orderIds)
                .orderByDesc(Order::getCreatedAt);
        if (status != null) {
            wrapper.eq(Order::getStatus, status);
        }
        return this.list(wrapper).stream().map(this::toOrderDetailVO).toList();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void shipOrder(Long merchantUserId, Long orderId) {
        // 只允许发货包含本店商品的订单
        Order order = this.getById(orderId);
        if (order == null) {
            throw new BusinessException(ResultCode.ORDER_NOT_FOUND);
        }
        if (!containsMyProduct(orderId, merchantUserId)) {
            throw new BusinessException(ResultCode.ORDER_NOT_BELONG);
        }
        if (order.getStatus() != STATUS_UNSHIPPED) {
            throw new BusinessException(ResultCode.ORDER_STATUS_ERROR);
        }
        order.setStatus(STATUS_SHIPPED);
        order.setDeliverTime(java.time.LocalDateTime.now());
        this.updateById(order);
    }

    // ==================== 私有辅助方法 ====================

    /** 查订单并校验归属，防止越权查别人的订单 */
    private Order getOwnedOrder(Long userId, Long orderId) {
        Order order = this.getById(orderId);
        if (order == null) {
            throw new BusinessException(ResultCode.ORDER_NOT_FOUND);
        }
        if (!order.getUserId().equals(userId)) {
            throw new BusinessException(ResultCode.ORDER_NOT_BELONG);
        }
        return order;
    }

    /** 判断订单明细里是否包含该商家店铺的商品 */
    private boolean containsMyProduct(Long orderId, Long merchantUserId) {
        Shop shop = shopService.getOne(new LambdaQueryWrapper<Shop>()
                .eq(Shop::getUserId, merchantUserId));
        if (shop == null) {
            return false;
        }
        List<Long> productIds = productService.list(new LambdaQueryWrapper<Product>()
                        .eq(Product::getShopId, shop.getId()))
                .stream().map(Product::getId).toList();
        if (productIds.isEmpty()) {
            return false;
        }
        return orderItemService.count(new LambdaQueryWrapper<OrderItem>()
                .eq(OrderItem::getOrderId, orderId)
                .in(OrderItem::getProductId, productIds)) > 0;
    }

    private OrderVO toOrderVO(Order order) {
        OrderVO vo = new OrderVO();
        vo.setId(order.getId());
        vo.setOrderNo(order.getOrderNo());
        vo.setTotalAmount(order.getTotalAmount());
        vo.setStatus(order.getStatus());
        vo.setReceiverName(order.getReceiverName());
        vo.setReceiverPhone(order.getReceiverPhone());
        vo.setCreatedAt(order.getCreatedAt());
        vo.setPayTime(order.getPayTime());
        return vo;
    }

    private OrderDetailVO toOrderDetailVO(Order order) {
        OrderDetailVO vo = new OrderDetailVO();
        vo.setId(order.getId());
        vo.setOrderNo(order.getOrderNo());
        vo.setUserId(order.getUserId());
        vo.setReceiverName(order.getReceiverName());
        vo.setReceiverPhone(order.getReceiverPhone());
        vo.setReceiverAddress(order.getReceiverAddress());
        vo.setTotalAmount(order.getTotalAmount());
        vo.setStatus(order.getStatus());
        vo.setPayTime(order.getPayTime());
        vo.setDeliverTime(order.getDeliverTime());
        vo.setFinishTime(order.getFinishTime());
        vo.setCancelTime(order.getCancelTime());
        vo.setRemark(order.getRemark());
        vo.setCreatedAt(order.getCreatedAt());

        List<OrderItem> items = orderItemService.list(new LambdaQueryWrapper<OrderItem>()
                .eq(OrderItem::getOrderId, order.getId()));
        vo.setItems(items.stream().map(item -> {
            OrderItemVO itemVO = new OrderItemVO();
            itemVO.setId(item.getId());
            itemVO.setProductId(item.getProductId());
            itemVO.setProductName(item.getProductName());
            itemVO.setProductImage(item.getProductImage());
            itemVO.setPrice(item.getPrice());
            itemVO.setQuantity(item.getQuantity());
            itemVO.setTotalPrice(item.getTotalPrice());
            return itemVO;
        }).toList());
        return vo;
    }
}
