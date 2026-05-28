package com.enshi.springmart.config;

import com.enshi.springmart.common.security.JwtInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

public class WebMvcConfig implements WebMvcConfigurer {
    @Autowired
    private JwtInterceptor jwtInterceptor;
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(jwtInterceptor)
                .addPathPatterns("/api/v1/**") // 拦截所有的业务接口
                .excludePathPatterns(
                        "/api/v1/user/login",    // 放行登录接口
                        "/api/v1/user/register", // 放行注册接口
                        "/api/v1/goods/**"       // 放行美食商品浏览接口（没登录也能看商品吧）
                );
    }
}
