package com.enshi.springmart.common.security;

import com.enshi.springmart.common.exception.BusinessException;
import com.enshi.springmart.common.result.ResultCode;
import com.enshi.springmart.utils.UserContext;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

/**
 * 管理端专用拦截器，只拦 /api/v1/admin/**
 * 它排在 JwtInterceptor 后面执行，那时候 UserContext 里已经有登录信息了，
 * 这里只负责最后一道判断：角色必须是管理员（role=2）。
 */
@Component
public class AdminInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
            throws Exception {
        // 放行预检请求
        if ("OPTIONS".equalsIgnoreCase(request.getMethod())) {
            return true;
        }

        Integer role = UserContext.getRole();
        if (role == null || role != 2) {
            throw new BusinessException(ResultCode.FORBIDDEN.getCode(), "仅限管理员操作");
        }
        return true;
    }
}
