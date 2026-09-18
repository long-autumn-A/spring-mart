package com.enshi.springmart.controller;

import com.enshi.springmart.common.result.Result;
import com.enshi.springmart.dto.ChangePasswordDTO;
import com.enshi.springmart.dto.UserLoginDTO;
import com.enshi.springmart.dto.UserRegisterDTO;
import com.enshi.springmart.dto.UserUpdateDTO;
import com.enshi.springmart.redis.TokenRedisService;
import com.enshi.springmart.service.UserService;
import com.enshi.springmart.utils.UserContext;
import com.enshi.springmart.vo.LoginResultV0;
import com.enshi.springmart.vo.UserV0;
import jakarta.validation.Valid;
import jakarta.annotation.Resource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

// 用户相关的接口都在这里
@RestController
@RequestMapping("/api/v1/user")
public class UserController {
    @Autowired
    private UserService userService;

    @Resource
    private TokenRedisService tokenRedisService;

    // 注册
    @PostMapping("/register")
    public Result<UserV0> register(@Valid @RequestBody UserRegisterDTO registerDTO) {
        UserV0 userVO = userService.register(registerDTO);
        return Result.success("注册成功", userVO);
    }

    // 登录
    @PostMapping("/login")
    public Result<LoginResultV0> login(@Valid @RequestBody UserLoginDTO loginDTO) {
        LoginResultV0 loginResult = userService.login(loginDTO);
        return Result.success("登录成功", loginResult);
    }

    // 获取当前登录用户自己的信息
    @GetMapping("/info")
    public Result<UserV0> getUserInfo() {
        Long userId = UserContext.getUserId();
        UserV0 userVO = userService.getUserById(userId);
        return Result.success(userVO);
    }

    // 修改个人资料
    @PutMapping("/update")
    public Result<UserV0> updateInfo(@Valid @RequestBody UserUpdateDTO updateDTO) {
        UserV0 userVO = userService.updateUserInfo(UserContext.getUserId(), updateDTO);
        return Result.success("资料更新成功", userVO);
    }

    // 修改密码
    @PutMapping("/password")
    public Result<?> changePassword(@Valid @RequestBody ChangePasswordDTO changePasswordDTO) {
        Long userId = UserContext.getUserId();
        userService.changePassword(userId, changePasswordDTO);
        return Result.success("密码修改成功，请使用新密码重新登录", null);
    }

    // 退出登录：把当前这条会话从 Redis 白名单里删掉，这个 token 立刻就失效了
    @PostMapping("/logout")
    public Result<?> logout() {
        tokenRedisService.removeToken(UserContext.getJti(), UserContext.getUserId());
        return Result.success("已退出登录", null);
    }
}
