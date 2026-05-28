package com.enshi.springmart.vo;

import lombok.Data;

import java.io.Serializable;

@Data
public class LoginResultV0 implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * 颁发给前端的身份令牌 (JWT Token)
     * 前端收到后需要本地缓存（如 LocalStorage），并在后续请求时塞入 HTTP Header 的 Authorization 中
     */
    private String token;

    /**
     * 登录用户的基本信息
     * 包含用户ID、用户名、昵称、头像等（不含密码等敏感数据），方便前端直接在商城右上角渲染
     */
    private UserV0 userInfo;
}
