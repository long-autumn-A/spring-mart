package com.enshi.springmart.common.security;

import jakarta.annotation.Resource;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.Collection;
import java.util.Map;
import java.util.Set;

@Component
public class RedisUtil {
    @Resource
    private StringRedisTemplate stringRedisTemplate;

//    string操作
    /** 设置值 */
    public void set(String key, String value) {
        stringRedisTemplate.opsForValue().set(key, value);
    }

    /** 设置值并携带过期时间 */
    public void set(String key, String value, Duration timeout) {
        stringRedisTemplate.opsForValue().set(key, value, timeout);
    }

    /** 获取值 */
    public String get(String key) {
        return stringRedisTemplate.opsForValue().get(key);
    }

    /** 设置值并返回是否成功（用于 setnx） */
    public boolean setIfAbsent(String key, String value, Duration timeout) {
        Boolean result = stringRedisTemplate.opsForValue().setIfAbsent(key, value, timeout);
        return Boolean.TRUE.equals(result);
    }

    /**
     * 自增
     */
    public Long increment(String key){
        Long value = stringRedisTemplate.opsForValue().increment(key);
        return value == null ? 0 : value;
    }

    /**
     * 自增并设置首次过期
     */
    public Long incrementWithExpireTime(String key,Duration timeout){
        Long value = stringRedisTemplate.opsForValue().increment(key);
        if (value !=null && value == 1){
            stringRedisTemplate.expire(key, timeout);
        }
        return value == null ? 0 : value;
    }

//    key操作
    /**
     * 删除key
     */
    public boolean delete(String key) {
       Boolean result = stringRedisTemplate.delete(key);
       return Boolean.TRUE.equals(result);
    }

    /**
     * 批量删除
     */
    public long delete(Collection<String> keys) {
        Long result = stringRedisTemplate.delete(keys);
        return result == null ? 0 : result;
    }

    /** 判断 key 是否存在 */
    public boolean hasKey(String key) {
        Boolean result = stringRedisTemplate.hasKey(key);
        return Boolean.TRUE.equals(result);
    }

    /** 设置过期时间 */
    public boolean expire(String key, Duration timeout) {
        Boolean result = stringRedisTemplate.expire(key, timeout);
        return Boolean.TRUE.equals(result);
    }

    /** 获取剩余过期时间（秒） */
    public long getExpire(String key) {
        Long expire = stringRedisTemplate.getExpire(key);
        return expire == null ? -1 : expire;
    }

    // ========== Hash 操作 ==========

    /** 存 Hash 全部字段 */
    public void hPutAll(String key, Map<String, String> map) {
        stringRedisTemplate.opsForHash().putAll(key, map);
    }

    /** 取 Hash 全部字段 */
    public Map<Object, Object> hGetAll(String key) {
        return stringRedisTemplate.opsForHash().entries(key);
    }

    /** 取单个字段 */
    public String hGet(String key, String field) {
        Object value = stringRedisTemplate.opsForHash().get(key, field);
        return value == null ? null : value.toString();
    }

    /** 存单个字段 */
    public void hPut(String key, String field, String value) {
        stringRedisTemplate.opsForHash().put(key, field, value);
    }

    /** 删除字段 */
    public void hDelete(String key, String... fields) {
        stringRedisTemplate.opsForHash().delete(key, (Object[]) fields);
    }

    // ========== Set 操作 ==========

    /** 添加 Set 元素 */
    public void sAdd(String key, String... values) {
        stringRedisTemplate.opsForSet().add(key, values);
    }

    /** 获取 Set 全部元素 */
    public Set<String> sMembers(String key) {
        return stringRedisTemplate.opsForSet().members(key);
    }

    /** 移除 Set 元素 */
    public void sRemove(String key, String... values) {
        stringRedisTemplate.opsForSet().remove(key, (Object[]) values);
    }

    /** Set 大小 */
    public long sSize(String key) {
        Long size = stringRedisTemplate.opsForSet().size(key);
        return size == null ? 0 : size;
    }
}
