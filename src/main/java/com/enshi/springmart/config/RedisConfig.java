package com.enshi.springmart.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import org.springframework.data.redis.serializer.JacksonJsonRedisSerializer;

import tools.jackson.databind.ObjectMapper;

@Configuration
public class RedisConfig {

    @Bean
    public RedisTemplate<String, Object> redisTemplate(
            RedisConnectionFactory factory,
            ObjectMapper objectMapper) {

        RedisTemplate<String, Object> template = new RedisTemplate<>();

        template.setConnectionFactory(factory);


        // key使用String
        StringRedisSerializer stringSerializer =
                new StringRedisSerializer();


        // value使用JSON
        JacksonJsonRedisSerializer<Object> jsonSerializer =
                new JacksonJsonRedisSerializer<>(
                        objectMapper,
                        Object.class
                );


        template.setKeySerializer(stringSerializer);

        template.setHashKeySerializer(stringSerializer);


        template.setValueSerializer(jsonSerializer);

        template.setHashValueSerializer(jsonSerializer);


        template.afterPropertiesSet();

        return template;
    }
}