package com.enshi.springmart;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

// 🔒 换成 excludeName，直接用类的全限定名字符串来排除，不需要任何 import！
@SpringBootApplication(excludeName = { "org.springframework.boot.autoconfigure.security.servlet.UserDetailsServiceAutoConfiguration" })
@MapperScan("com.enshi.springmart.mapper")
public class SpringMartApplication {

    public static void main(String[] args) {
        SpringApplication.run(SpringMartApplication.class, args);
    }
}