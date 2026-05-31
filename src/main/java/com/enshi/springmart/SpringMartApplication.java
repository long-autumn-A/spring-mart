package com.enshi.springmart;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

// 启动类，排除了 Spring Security 默认的 UserDetailsService，因为我们自己用 JWT
@SpringBootApplication(excludeName = {
        "org.springframework.boot.autoconfigure.security.servlet.UserDetailsServiceAutoConfiguration"
})
@MapperScan("com.enshi.springmart.mapper")
public class SpringMartApplication {

    public static void main(String[] args) {
        SpringApplication.run(SpringMartApplication.class, args);
    }
}
