package com.enshi.springmart.common.security;

import com.enshi.springmart.common.exception.BusinessException;
import com.enshi.springmart.common.result.ResultCode;
import com.enshi.springmart.utils.UserContext;
import io.jsonwebtoken.Claims;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

// 拦截器，每个请求进来先检查有没有带合法的 token，解析后存入 UserContext
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
            Long userId = Long.valueOf(claims.getSubject());
            Integer role = Integer.valueOf(claims.get("role", String.class));

            // 存入 request（保留兼容，后续可以逐步移除）
            request.setAttribute("currentUserId", claims.getSubject());
            request.setAttribute("currentUserRole", claims.get("role", String.class));

            // 存入 ThreadLocal，后续 Controller/Service 任意地方都能取
            UserContext.set(userId, role);
            return true;
        } catch (Exception e) {
            throw new BusinessException(ResultCode.UNAUTHORIZED.getCode(), "Token已过期或不合法，请重新登录");
        }
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response,
                                Object handler, Exception ex) {
        // 请求结束，清理 ThreadLocal，防止内存泄漏
        UserContext.clear();
    }
}
