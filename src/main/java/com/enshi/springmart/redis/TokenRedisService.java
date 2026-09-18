package com.enshi.springmart.redis;

import jakarta.annotation.Resource;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Component
public class TokenRedisService {
    @Resource
    private StringRedisTemplate redis;

    private static final String TOKEN_KEY = "auth:token:";
    private static final String USER_TOKENS_KEY = "auth:user:%s:tokens";
    // 调用方没传有效期时的兜底值，正常都按 JWT 的真实剩余有效期来
    private static final Duration DEFAULT_TOKEN_TTL = Duration.ofHours(2);
    private static final Duration USER_TOKENS_TTL = Duration.ofDays(7);
    // 白名单剩余时间和 JWT 剩余时间差超过这个秒数才刷新，免得每个请求都往 Redis 写一次
    private static final long RENEW_THRESHOLD_SECONDS = 60;

    /**
     * 保存 Token 白名单，tokenTTL 传 JWT 的剩余有效期
     */
    public void saveToken(String jti, Long userId, Map<String, String> session, Duration tokenTTL) {
        Duration ttl = (tokenTTL == null || tokenTTL.isZero() || tokenTTL.isNegative())
                ? DEFAULT_TOKEN_TTL : tokenTTL;

        String tokenKey = TOKEN_KEY + jti;
        redis.opsForHash().putAll(tokenKey, session);
        redis.expire(tokenKey, ttl);

        String userTokensKey = String.format(USER_TOKENS_KEY, userId);
        redis.opsForSet().add(userTokensKey, jti);
        redis.expire(userTokensKey, USER_TOKENS_TTL);
    }

    /**
     * 校验 Token 是否存在
     */
    public boolean exists(String jti) {
        return jti != null && Boolean.TRUE.equals(redis.hasKey(TOKEN_KEY + jti));
    }

    /**
     * 获取会话信息
     */
    public Map<Object, Object> getSession(String jti) {
        return redis.opsForHash().entries(TOKEN_KEY + jti);
    }

    /**
     * 把白名单的过期时间对齐到 JWT 的剩余有效期。
     * 注意：这里只延长 Redis 里这条会话，不改变 JWT 自己的过期时间，
     * 所以两者不会互相矛盾（JWT 到期就是到期）。
     */
    public void renewIfNeeded(String jti, Duration remain) {
        if (jti == null || remain == null || remain.isZero() || remain.isNegative()) {
            return;
        }
        String tokenKey = TOKEN_KEY + jti;
        Long ttl = redis.getExpire(tokenKey);   // 秒；-1 表示没设置过期时间，-2 表示 key 已经不在了
        if (ttl == null || ttl < 0) {
            return;
        }
        if (remain.getSeconds() - ttl > RENEW_THRESHOLD_SECONDS) {
            redis.expire(tokenKey, remain);
        }
    }

    /**
     * 退出登录：删除单个 Token
     */
    public void removeToken(String jti, Long userId) {
        if (jti == null) {
            return;
        }
        redis.delete(TOKEN_KEY + jti);
        if (userId != null) {
            redis.opsForSet().remove(String.format(USER_TOKENS_KEY, userId), jti);
        }
    }

    /**
     * 强制下线：删除该用户所有 Token
     */
    public void removeAllTokens(Long userId) {
        String userTokensKey = String.format(USER_TOKENS_KEY, userId);
        Set<String> jtis = redis.opsForSet().members(userTokensKey);

        if (jtis != null && !jtis.isEmpty()) {
            List<String> keys = jtis.stream()
                    .map(jti -> TOKEN_KEY + jti)
                    .collect(Collectors.toList());
            redis.delete(keys);
        }
        redis.delete(userTokensKey);
    }
}
