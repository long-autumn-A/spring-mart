package com.enshi.springmart.common.security;

import com.enshi.springmart.common.exception.BusinessException;
import com.enshi.springmart.common.result.ResultCode;
import io.jsonwebtoken.Claims;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

// 拦截器，每个请求进来先检查有没有带合法的 token
@Component
public class JwtInterceptor implements HandlerInterceptor {

    private static final String BEARER_PREFIX = "Bearer ";

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String authHeader = request.getHeader("Authorization");

        // 没带 token，直接挡回去
        if (authHeader == null || authHeader.isEmpty()) {
            throw new BusinessException(ResultCode.UNAUTHORIZED.getCode(), "未登录或Token缺失，请先登录");
        }

        // 前端可能会带 "Bearer xxx" 格式，也可能只传 token 本身，都兼容一下
        String token = authHeader;
        if (authHeader.startsWith(BEARER_PREFIX)) {
            token = authHeader.substring(BEARER_PREFIX.length());
        }

        try {
            // 解析 token，不合法或过期了都会在这步炸
            Claims claims = JwtUtils.parseToken(token);
            // 把 userId 存到 request 里，后面 Controller 可以直接拿
            request.setAttribute("currentUserId", claims.getSubject());
            return true;
        } catch (Exception e) {
            throw new BusinessException(ResultCode.UNAUTHORIZED.getCode(), "Token已过期或不合法，请重新登录");
        }
    }
}
