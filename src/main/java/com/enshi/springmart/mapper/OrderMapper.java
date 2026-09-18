package com.enshi.springmart.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.enshi.springmart.entity.Order;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface OrderMapper extends BaseMapper<Order> {
}
