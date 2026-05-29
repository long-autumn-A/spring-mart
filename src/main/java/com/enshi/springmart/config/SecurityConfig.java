package com.enshi.springmart.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Collections;
import java.util.List;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                // 1. 🌐 核心补充：开启跨域支持，并将我们配置的跨域规则注入进来
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))

                // 2. ❌ 禁用 CSRF 防护（前后端分离项目靠 JWT 认证，不需要常规的 Cookie 级别的 CSRF 防护）
                .csrf(csrf -> csrf.disable())

                // 3. 🔓 白名单精准放行
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(
                                "/api/v1/user/login",
                                "/api/v1/user/register",
                                "/api/v1/goods/**"
                        ).permitAll()
                        .anyRequest().authenticated()
                )

                // 4. 🛡️ 禁用默认表单登录和 Basic 认证
                .formLogin(form -> form.disable())
                .httpBasic(basic -> basic.disable());

        return http.build();
    }

    /**
     * 🔗 核心新增：定义具体的跨域开放规则
     */
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();

        // 允许来自前端 Vue 专属端口 (5173) 的请求跨域访问
        configuration.setAllowedOrigins(List.of("http://localhost:5173"));

        // 允许所有的 HTTP 请求方法（GET, POST, PUT, DELETE, OPTIONS）
        configuration.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));

        // 允许携带任何请求头（如 Authorization, Content-Type 等）
        configuration.setAllowedHeaders(List.of("*"));

        // 允许前端 Axios 发送请求时携带 Cookie 凭证（如果需要的话）
        configuration.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        // 将上述规则应用到后端所有的 URL 接口路径上（/**）
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}