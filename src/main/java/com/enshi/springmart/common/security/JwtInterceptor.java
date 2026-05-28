package com.enshi.springmart.common.security;

import io.jsonwebtoken.Claims;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.servlet.HandlerInterceptor;

public class JwtInterceptor implements HandlerInterceptor {
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        //  从 HTTP 请求头中获取 Authorization
        String token = request.getHeader("Authorization");

        //  如果前端没传 Token，或者不是以标准的 Bearer 开头（可选），抛出未登录异常
        if (token == null || token.isEmpty()) {
            throw new RuntimeException("未登录或Token缺失，请先登录"); // 之后可以换成你的 BusinessException
        }

        try {
            //  解析 Token，如果过期或被篡改，这里会直接抛出异常
            Claims claims = JwtUtils.parseToken(token);

            //  将解析出来的 userId 存入 request 域中，方便后续 Controller 直接获取
            request.setAttribute("currentUserId", claims.getSubject());
            return true; // 放行
        } catch (Exception e) {
            throw new RuntimeException("Token已过期或不合法，请重新登录");
        }
    }
}
