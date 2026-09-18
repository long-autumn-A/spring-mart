package com.enshi.springmart.common.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.UUID;

// JWT 工具类，生成和解析 token 都在这
// 密钥和过期时间由 JwtConfig 在启动时从配置文件注入（jwt.secret / jwt.expiration）
public class JwtUtils {

    private static String signKey = "EnShiTuJiaMeiShiDianShangPingTai2025SecretKey";

    private static long expireTime = 24 * 60 * 60 * 1000L;


    static void init(String key, long expiration) {
        JwtUtils.signKey = key;
        JwtUtils.expireTime = expiration;
    }

    private static SecretKey getKey() {
        return Keys.hmacShaKeyFor(signKey.getBytes(StandardCharsets.UTF_8));
    }

    /**
     * 生成 token，附带 jti（唯一 ID），用于 Redis 白名单
     */
    public static String createToken(String userId, String username, String role) {
        String jti = UUID.randomUUID().toString().replace("-", "");
        return Jwts.builder()
                .id(jti)                                  // 关键：jti
                .subject(userId)
                .claim("username", username)
                .claim("role", role)
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + expireTime))
                .signWith(getKey())
                .compact();
    }

    /**
     * 解析 token，过期或被篡改会抛异常
     */
    public static Claims parseToken(String token) {
        return Jwts.parser()
                .verifyWith(getKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    /**
     * 获取 token 的 jti
     */
    public static String getJti(String token) {
        return parseToken(token).getId();
    }

    /**
     * 获取 token 剩余有效期（毫秒），已过期返回 0
     */
    public static long getRemainMillis(String token) {
        Date expiration = parseToken(token).getExpiration();
        long remain = expiration.getTime() - System.currentTimeMillis();
        return Math.max(remain, 0);
    }
}