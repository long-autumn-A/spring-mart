package com.enshi.springmart.redis;

import jakarta.annotation.Resource;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.time.Duration;

@Component
public class CaptchaRedisService {
    @Resource
    private StringRedisTemplate redis;

    private static final String CAPTCHA_KEY = "auth:captcha:";
    private static final String CAPTCHA_LIMIT_KEY = "auth:captcha:limit:";
    private static final Duration CAPTCHA_TTL = Duration.ofMinutes(5);
    private static final Duration LIMIT_TTL = Duration.ofMinutes(1);
    private static final long LIMIT_MAX = 10;

    /** 保存验证码 */
    public void save(String uuid, String code) {
        redis.opsForValue().set(CAPTCHA_KEY + uuid, code, CAPTCHA_TTL);
    }

    /**
     * 校验并删除验证码（一次性）
     * 用 getAndDelete 一步完成「取值 + 删除」，避免并发下同一个验证码被用两次
     */
    public boolean validate(String uuid, String inputCode) {
        if (uuid == null || uuid.isBlank() || inputCode == null || inputCode.isBlank()) {
            return false;
        }
        String code = redis.opsForValue().getAndDelete(CAPTCHA_KEY + uuid);

        return code != null && code.equalsIgnoreCase(inputCode);
    }

    /**
     * 生成接口限流：同一 IP 每分钟最多 10 次
     */
    public boolean tryAcquire(String ip) {
        String key = CAPTCHA_LIMIT_KEY + ip;
        Long count = redis.opsForValue().increment(key);
        if (count != null && count == 1) {
            redis.expire(key, LIMIT_TTL);
        } else {
            // 兜底：万一上一次没来得及设置过期时间，key 会永远留在 Redis 里
            Long ttl = redis.getExpire(key);
            if (ttl != null && ttl < 0) {
                redis.expire(key, LIMIT_TTL);
            }
        }
        return count != null && count <= LIMIT_MAX;
    }
}

