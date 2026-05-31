package com.enshi.springmart.vo;

import lombok.Data;

import java.io.Serializable;

// 登录成功后返回给前端的东西，包含 token 和用户信息
@Data
public class LoginResultV0 implements Serializable {
    private static final long serialVersionUID = 1L;

    // JWT 令牌，前端拿到之后要存起来，以后每次请求都要带在 Header 里
    private String token;

    // 当前登录用户的基本信息，不含密码
    private UserV0 userInfo;
}
