package com.enshi.springmart;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;

import java.util.concurrent.TimeUnit;

import static org.hibernate.validator.internal.util.Contracts.assertTrue;
import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
public class RedisTest {
    @Autowired
    private StringRedisTemplate redis;

    @Autowired
    private RedisConnectionFactory connectionFactory;

    /** 打印底层用的客户端，确认是 Jedis */
    @Test
    void printClientType() {
        String clientName = connectionFactory.getClass().getName();
        System.out.println("Redis 客户端: " + clientName);
        assertTrue(clientName.contains("jedis"),
                "期望使用 Jedis，实际是: " + clientName);
    }

    /** 基础读写 */
    @Test
    void basicSetGet() {
        String key = "test:basic";
        redis.opsForValue().set(key, "hello", 60, TimeUnit.SECONDS);
        String value = redis.opsForValue().get(key);
        assertEquals("hello", value);
        redis.delete(key);
    }

    /** ping 一下服务端 */
    @Test
    void ping() {
        String pong = redis.getConnectionFactory()
                .getConnection()
                .ping();
        assertEquals("PONG", pong);
    }
}
