package com.enshi.springmart.config;

import com.enshi.springmart.common.security.JwtInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

// 注册 JWT 拦截器，设置哪些路径要拦截、哪些不用
@Configuration
public class WebMvcConfig implements WebMvcConfigurer {
    @Autowired
    private JwtInterceptor jwtInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(jwtInterceptor)
                .addPathPatterns("/api/v1/**")       // 所有业务接口都要过拦截器
                .excludePathPatterns(
                        "/api/v1/user/login",         // 登录不用鉴权
                        "/api/v1/user/register",      // 注册不用鉴权
                        "/api/v1/categories/list",    // 查看分类树不用登录
                        "/api/v1/categories/flat",    // 扁平分类列表也不用登录
                        "/api/v1/goods/**"            // 浏览商品不用登录
                );
    }
}
