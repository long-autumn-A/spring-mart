// OrderItemServiceImpl.java
package com.enshi.springmart.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.enshi.springmart.entity.OrderItem;
import com.enshi.springmart.mapper.OrderItemMapper;
import com.enshi.springmart.service.OrderItemService;
import org.springframework.stereotype.Service;

@Service
public class OrderItemServiceImpl extends ServiceImpl<OrderItemMapper, OrderItem> implements OrderItemService {
}