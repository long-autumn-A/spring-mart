package com.enshi.springmart.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.io.Serializable;

@Data
public class UserLoginDTO implements Serializable {
    private static final long serialVersionUID = 1L;

    @NotBlank(message = "账号（用户名/手机号）不能为空")
    private String account;

    @NotBlank(message = "密码不能为空")
    private String password;
}
