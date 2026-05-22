package com.enshi.springmart;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan("com.enshi.springmart.mapper")
public class SpringMartApplication {

    public static void main(String[] args) {
        SpringApplication.run(SpringMartApplication.class, args);
    }

}
