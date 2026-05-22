package com.enshi.springmart.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.argon2.Argon2PasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
public class CryptoConfig {

    @Bean
    public PasswordEncoder passwordEncoder(
            @Value("${crypto.argon2.salt-length}") int saltLength,
            @Value("${crypto.argon2.hash-length}") int hashLength,
            @Value("${crypto.argon2.parallelism}") int parallelism,
            @Value("${crypto.argon2.memory}") int memory,
            @Value("${crypto.argon2.iterations}") int iterations) {
        return new Argon2PasswordEncoder(saltLength, hashLength, parallelism, memory, iterations);
    }
}

