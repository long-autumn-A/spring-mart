package com.enshi.springmart.common.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

public class JwtUtils {
//    密钥
    private static final String SIGN_KEY = "EnShiTuJiaMeiShiShopSpringMartTokenKey2026";
//    过期时间7天
private static final long EXPIRE_TIME = 7 * 24 * 60 * 60 * 1000L;

/**
 * 生成JWT Token
 */
public static String createToken(String userId, String username) {
    SecretKey key = Keys.hmacShaKeyFor(SIGN_KEY.getBytes(StandardCharsets.UTF_8));
    return Jwts.builder()
            .subject(userId)
            .claim("username", username)
            .issuedAt(new java.util.Date())
            .expiration(new java.util.Date(System.currentTimeMillis() + EXPIRE_TIME))
            .signWith(key)
            .compact();
    }
/**
 * 解析并验证 Token
 */
public static Claims parseToken(String token) {
    SecretKey key = Keys.hmacShaKeyFor(SIGN_KEY.getBytes(StandardCharsets.UTF_8));
    return Jwts.parser()
            .verifyWith(key) // 替代了以前的 setSigningKey()
            .build()         // 这一步是新版必须要点出来的构建器
            .parseSignedClaims(token) // 替代了以前的 parseClaimsJws()
            .getPayload();
    }
}
