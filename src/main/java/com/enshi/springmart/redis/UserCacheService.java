package com.enshi.springmart.redis;

import com.enshi.springmart.vo.UserV0;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;
import tools.jackson.databind.ObjectMapper;

import java.time.Duration;

/**
 * 用户信息缓存。
 * 缓存里存的是 UserV0 的 JSON，以后 VO 加减字段不用动这里。
 * 注意：缓存只是加速手段，Redis 挂了也不该影响业务，
 * 所以读写异常一律吞掉记日志，让调用方回源查库。
 */
@Slf4j
@Component
public class UserCacheService {
    @Resource
    private StringRedisTemplate redis;

    @Resource
    private ObjectMapper objectMapper;

    private static final String USER_INFO_KEY = "user:info:";
    private static final Duration USER_INFO_TTL = Duration.ofMinutes(30);

    /** 缓存用户信息 */
    public void save(Long userId, UserV0 userInfo) {
        if (userId == null || userInfo == null) {
            return;
        }
        try {
            redis.opsForValue().set(USER_INFO_KEY + userId,
                    objectMapper.writeValueAsString(userInfo), USER_INFO_TTL);
        } catch (Exception e) {
            log.warn("写用户缓存失败，不影响本次请求: userId={}, err={}", userId, e.getMessage());
        }
    }

    /** 获取用户信息，没有缓存（或 Redis 异常）就返回 null，由调用方回源查库 */
    public UserV0 get(Long userId) {
        if (userId == null) {
            return null;
        }
        try {
            String json = redis.opsForValue().get(USER_INFO_KEY + userId);
            return json == null ? null : objectMapper.readValue(json, UserV0.class);
        } catch (Exception e) {
            log.warn("读用户缓存失败，回源查库: userId={}, err={}", userId, e.getMessage());
            return null;
        }
    }

    /** 修改用户信息后删除缓存 */
    public void delete(Long userId) {
        if (userId == null) {
            return;
        }
        try {
            redis.delete(USER_INFO_KEY + userId);
        } catch (Exception e) {
            log.warn("删用户缓存失败: userId={}, err={}", userId, e.getMessage());
        }
    }
}
