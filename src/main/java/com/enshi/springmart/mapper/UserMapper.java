package com.enshi.springmart.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.enshi.springmart.entity.User;
import org.apache.ibatis.annotations.Mapper;

// 用户表的 Mapper，继承 BaseMapper 之后基本的增删改查都有了
@Mapper
public interface UserMapper extends BaseMapper<User> {

}
