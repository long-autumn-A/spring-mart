package com.enshi.springmart.common.security;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * 启动时把配置文件里的 jwt.secret / jwt.expiration 注入 JwtUtils，
 * 避免 JWT 签名密钥硬编码在代码里。密钥长度必须 >= 32 字节（HS256 要求）。
 */
@Component
public class JwtConfig {

    public JwtConfig(@Value("${jwt.secret}") String secret,
                     @Value("${jwt.expiration}") long expiration) {
        if (secret == null || secret.getBytes(java.nio.charset.StandardCharsets.UTF_8).length < 32) {
            throw new IllegalStateException("jwt.secret 长度必须不少于 32 字节");
        }
        JwtUtils.init(secret, expiration);
    }
}
