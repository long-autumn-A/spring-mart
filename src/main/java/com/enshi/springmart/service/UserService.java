package com.enshi.springmart.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.enshi.springmart.dto.ChangePasswordDTO;
import com.enshi.springmart.dto.UserLoginDTO;
import com.enshi.springmart.dto.UserRegisterDTO;
import com.enshi.springmart.dto.UserUpdateDTO;
import com.enshi.springmart.entity.User;
import com.enshi.springmart.vo.LoginResultV0;
import com.enshi.springmart.vo.UserV0;

// 用户相关的业务逻辑接口，继承 MyBatis-Plus 的 IService 省掉基本的增删改查
public interface UserService extends IService<User> {

    // 注册
    UserV0 register(UserRegisterDTO registerDTO);

    // 登录，返回 token 和用户信息
    LoginResultV0 login(UserLoginDTO loginDTO);

    // 改资料（昵称、手机号、邮箱、头像）
    UserV0 updateUserInfo(UserUpdateDTO updateDTO);

    // 查某个用户的信息
    UserV0 getUserById(Long userId);

    // 改密码，要验证旧密码
    void changePassword(Long userId, ChangePasswordDTO changePasswordDTO);
}
