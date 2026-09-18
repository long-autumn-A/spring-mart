package com.enshi.springmart.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.io.Serializable;

// 登录的时候前端传过来的数据
@Data
public class UserLoginDTO implements Serializable {
    private static final long serialVersionUID = 1L;

    @NotBlank(message = "账号（用户名/手机号）不能为空")
    private String account; // 可以是用户名也可以是手机号

    @NotBlank(message = "密码不能为空")
    private String password;

    @NotBlank(message = "验证码不能为空")
    private String code;   // 用户输入的验证码

    @NotBlank(message = "验证码标识不能为空")
    private String uuid;   // 获取验证码时返回的 uuid

}
