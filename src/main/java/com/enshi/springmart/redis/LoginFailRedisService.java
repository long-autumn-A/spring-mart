package com.enshi.springmart.redis;

import jakarta.annotation.Resource;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.time.Duration;

@Component
public class LoginFailRedisService {
    @Resource
    private StringRedisTemplate redis;

    private static final String FAIL_KEY = "auth:fail:";
    private static final int MAX_FAIL = 5;
    private static final Duration LOCK_TTL = Duration.ofMinutes(15);

    /** 记录一次失败，返回当前失败次数 */
    public long recordFail(String username) {
        String key = FAIL_KEY + username;
        Long count = redis.opsForValue().increment(key);
        if (count != null && count == 1) {
            redis.expire(key, LOCK_TTL);
        } else {
            // 兜底：保证计数 key 一定有过期时间，不会永远留在 Redis 里
            Long ttl = redis.getExpire(key);
            if (ttl != null && ttl < 0) {
                redis.expire(key, LOCK_TTL);
            }
        }
        return count == null ? 0 : count;
    }

    /** 是否已锁定 */
    public boolean isLocked(String username) {
        String value = redis.opsForValue().get(FAIL_KEY + username);
        if (value == null) {
            return false;
        }
        try {
            return Long.parseLong(value) >= MAX_FAIL;
        } catch (NumberFormatException e) {
            // 值被人手动改坏时按未锁定处理，避免把用户永久挡在门外
            return false;
        }
    }

    /** 锁定时还剩多少秒，用于给前端提示「请 X 分钟后再试」 */
    public long getRemainLockSeconds(String username) {
        Long ttl = redis.getExpire(FAIL_KEY + username);
        return ttl == null || ttl < 0 ? 0 : ttl;
    }

    /** 锁定提示文案，失败次数上限和锁定时长都从这里取，避免文案和配置对不上 */
    public String getLockedMessage(long remainSeconds) {
        long minutes = Math.max(1, (remainSeconds + 59) / 60);
        return "密码连续输错 " + MAX_FAIL + " 次，账号已锁定，请 " + minutes + " 分钟后再试";
    }

    /** 登录成功清除失败记录 */
    public void clear(String username) {
        redis.delete(FAIL_KEY + username);
    }
}
