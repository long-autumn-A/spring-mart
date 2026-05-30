package com.enshi.springmart.common.security;

import com.enshi.springmart.common.exception.BusinessException;
import com.enshi.springmart.common.result.ResultCode;
import io.jsonwebtoken.Claims;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
public class JwtInterceptor implements HandlerInterceptor {

    private static final String BEARER_PREFIX = "Bearer ";

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        // 从 HTTP 请求头中获取 Authorization
        String authHeader = request.getHeader("Authorization");

        // 如果前端没传 Token，抛出未登录异常
        if (authHeader == null || authHeader.isEmpty()) {
            throw new BusinessException(ResultCode.UNAUTHORIZED.getCode(), "未登录或Token缺失，请先登录");
        }

        // 兼容 "Bearer xxx" 和纯 token 两种格式
        String token = authHeader;
        if (authHeader.startsWith(BEARER_PREFIX)) {
            token = authHeader.substring(BEARER_PREFIX.length());
        }

        try {
            // 解析 Token，如果过期或被篡改，这里会直接抛出异常
            Claims claims = JwtUtils.parseToken(token);

            // 将解析出来的 userId 存入 request 域中，方便后续 Controller 直接获取
            request.setAttribute("currentUserId", claims.getSubject());
            return true; // 放行
        } catch (Exception e) {
            throw new BusinessException(ResultCode.UNAUTHORIZED.getCode(), "Token已过期或不合法，请重新登录");
        }
    }
}
