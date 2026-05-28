package com.enshi.springmart.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.enshi.springmart.dto.UserLoginDTO;
import com.enshi.springmart.dto.UserRegisterDTO;
import com.enshi.springmart.dto.UserUpdateDTO;
import com.enshi.springmart.entity.User;
import com.enshi.springmart.vo.UserV0;

public interface UserService extends IService<User> {
    /**
     * 用户注册
     */
    UserV0 register(UserRegisterDTO registerDTO);

    /**
     * 用户登录
     */
    UserV0 login(UserLoginDTO loginDTO);

    /**
     * 更新用户信息
     */
    UserV0 updateUserInfo(UserUpdateDTO updateDTO);
}
