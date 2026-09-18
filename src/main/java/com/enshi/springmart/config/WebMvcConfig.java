package com.enshi.springmart.config;

import com.enshi.springmart.common.security.AdminInterceptor;
import com.enshi.springmart.common.security.JwtInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

// 注册 JWT 拦截器，设置哪些路径要拦截、哪些不用
@Configuration
public class WebMvcConfig implements WebMvcConfigurer {
    @Autowired
    private JwtInterceptor jwtInterceptor;

    @Autowired
    private AdminInterceptor adminInterceptor;

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // 映射 /uploads/** 到本地 ./uploads/ 目录，让上传的图片可以直接通过 URL 访问
        registry.addResourceHandler("/uploads/**")
                .addResourceLocations("file:./uploads/");
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        // 顺序很重要：先过 JwtInterceptor（验 token + 白名单，把用户信息放进 UserContext），
        // 再过 AdminInterceptor（管理端接口要求 role=2）
        registry.addInterceptor(jwtInterceptor)
                .addPathPatterns("/api/v1/**")       // 所有业务接口都要过拦截器
                .excludePathPatterns(
                        "/api/v1/user/login",         // 登录不用鉴权
                        "/api/v1/user/register",      // 注册不用鉴权
                        "/api/v1/user/captcha",       // 登录验证码不用鉴权（这时候用户还没登录）
                        "/api/v1/categories/list",    // 查看分类树不用登录
                        "/api/v1/categories/flat",    // 扁平分类列表也不用登录
                        "/api/v1/goods/list",         // 商品列表公开
                        "/api/v1/goods/detail/**",    // 商品详情公开
                        "/api/v1/goods/*/images",     // 商品轮播图公开
                        "/api/v1/shop/list",          // 店铺列表不用登录
                        "/api/v1/shop/detail/**"      // 店铺详情不用登录
                );

        // 管理端所有接口都要求管理员身份
        registry.addInterceptor(adminInterceptor)
                .addPathPatterns("/api/v1/admin/**");
    }
}
