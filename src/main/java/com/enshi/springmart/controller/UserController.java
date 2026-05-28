package com.enshi.springmart.controller;

import com.enshi.springmart.common.result.Result;
import com.enshi.springmart.dto.UserLoginDTO;
import com.enshi.springmart.dto.UserRegisterDTO;
import com.enshi.springmart.dto.UserUpdateDTO;
import com.enshi.springmart.service.UserService;
import com.enshi.springmart.vo.LoginResultV0;
import com.enshi.springmart.vo.UserV0;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * 恩施美食电商平台 - 用户控制层 API
 */
@RestController
@RequestMapping("/api/v1/user")
public class UserController {
    @Autowired
    private UserService userService;
    /**
     * 用户注册接口
     * @Valid 注解用于激活 DTO 中的 @NotBlank, @Pattern 等参数校验
     */
    @PostMapping("/register")
    public Result<UserV0> register(@Valid @RequestBody UserRegisterDTO registerDTO) {
        UserV0 userVO = userService.register(registerDTO);
        return Result.success("注册成功", userVO);
    }

    /**
     * 用户登录接口
     */
    @PostMapping("/login")
    public Result<LoginResultV0> login(@Valid @RequestBody UserLoginDTO loginDTO) {
        // 调用业务层，业务层内部已完成 Argon2 密码比对并借助 JwtUtils 签发了 Token
        LoginResultV0 loginResult = userService.login(loginDTO);

        // 统一返回标准响应体
        return Result.success("登录成功", loginResult);
    }

    /**
     * 修改个人资料接口
     */
    @PutMapping("/update")
    public Result<UserV0> updateInfo(@Valid @RequestBody UserUpdateDTO updateDTO) {
        UserV0 userVO = userService.updateUserInfo(updateDTO);
        return Result.success("资料更新成功", userVO);
    }
}
