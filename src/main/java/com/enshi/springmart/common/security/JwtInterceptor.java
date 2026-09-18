package com.enshi.springmart.common.security;

import com.enshi.springmart.common.exception.BusinessException;
import com.enshi.springmart.common.result.ResultCode;
import com.enshi.springmart.redis.TokenRedisService;
import com.enshi.springmart.utils.UserContext;
import io.jsonwebtoken.Claims;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import java.time.Duration;

// 拦截器，每个请求进来先检查有没有带合法的 token
// 校验两件事：① JWT 本身的签名和过期时间 ② Redis 白名单里这条会话还在不在（退出登录/被强制下线后就不在了）
@Component
public class JwtInterceptor implements HandlerInterceptor {

    private static final String BEARER_PREFIX = "Bearer ";

    @Resource
    private TokenRedisService tokenRedisService;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        // 放行预检请求
        if ("OPTIONS".equalsIgnoreCase(request.getMethod())) {
            return true;
        }

        String authHeader = request.getHeader("Authorization");

        // 没带 token，直接挡回去
        if (authHeader == null || authHeader.isBlank()) {
            throw new BusinessException(ResultCode.UNAUTHORIZED.getCode(), "未登录或Token缺失，请先登录");
        }

        // 前端可能会带 "Bearer xxx" 格式，也可能只传 token 本身，都兼容一下
        String token = authHeader;
        if (authHeader.startsWith(BEARER_PREFIX)) {
            token = authHeader.substring(BEARER_PREFIX.length());
        }

        Claims claims;
        Long userId;
        Integer role;
        String jti;
        try {
            // 解析 token，不合法或过期了都会在这步炸
            claims = JwtUtils.parseToken(token);
            userId = Long.valueOf(claims.getSubject());
            role = Integer.valueOf(claims.get("role", String.class));
            jti = claims.getId();
        } catch (Exception e) {
            throw new BusinessException(ResultCode.UNAUTHORIZED.getCode(), "Token已过期或不合法，请重新登录");
        }

        // 白名单校验：退出登录、改密码、管理员强制下线后这里就查不到了
        if (jti == null || !tokenRedisService.exists(jti)) {
            throw new BusinessException(ResultCode.UNAUTHORIZED.getCode(), "登录状态已失效，请重新登录");
        }

        // 把白名单的过期时间对齐到 JWT 的剩余有效期，避免出现「token 没过期但 Redis 先把它踢了」
        long remainMillis = claims.getExpiration().getTime() - System.currentTimeMillis();
        if (remainMillis > 0) {
            tokenRedisService.renewIfNeeded(jti, Duration.ofMillis(remainMillis));
        }

        // 存入 request（保留兼容，后续可以逐步移除）
        request.setAttribute("currentUserId", claims.getSubject());
        request.setAttribute("currentUserRole", claims.get("role", String.class));

        // 存入 ThreadLocal，后续 Controller/Service 任意地方都能取
        UserContext.set(userId, role, jti, claims.get("username", String.class));
        return true;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response,
                                Object handler, Exception ex) {
        // 请求结束，清理 ThreadLocal，防止内存泄漏
        UserContext.clear();
    }
}
