package com.enshi.springmart.common.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

// JWT 工具类，生成和解析 token 都在这
public class JwtUtils {

    // 签名密钥，实际项目应该放配置里或者环境变量里，这里先写死
    private static final String SIGN_KEY = "EnShiTuJiaMeiShiShopSpringMartTokenKey2026";

    // 7 天过期
    private static final long EXPIRE_TIME = 7 * 24 * 60 * 60 * 1000L;

    // 根据用户 ID、用户名和角色生成 token
    public static String createToken(String userId, String username, String role) {
        SecretKey key = Keys.hmacShaKeyFor(SIGN_KEY.getBytes(StandardCharsets.UTF_8));
        return Jwts.builder()
                .subject(userId)
                .claim("username", username)
                .claim("role", role)
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + EXPIRE_TIME))
                .signWith(key)
                .compact();
    }

    // 解析 token，如果过期或者被改过会直接抛异常
    public static Claims parseToken(String token) {
        SecretKey key = Keys.hmacShaKeyFor(SIGN_KEY.getBytes(StandardCharsets.UTF_8));
        return Jwts.parser()
                .verifyWith(key)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }
}
