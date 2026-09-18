package com.enshi.springmart.utils;

/**
 * 当前登录用户上下文，基于 ThreadLocal 实现线程隔离。
 * JwtInterceptor 解析完 token 后调用 set() 存入，
 * 请求结束时调用 clear() 清理，防止内存泄漏。
 *
 * 使用方式：
 *   Long userId = UserContext.getUserId();
 *   Integer role = UserContext.getRole();
 *   String jti = UserContext.getJti();        // 退出登录时要删的那条会话
 *   String name = UserContext.getUsername();
 */
public class UserContext {

    private static final ThreadLocal<Long> USER_ID = new ThreadLocal<>();
    private static final ThreadLocal<Integer> USER_ROLE = new ThreadLocal<>();
    // 当前请求所用 token 的 jti，退出登录时要靠它把白名单里的那条记录删掉
    private static final ThreadLocal<String> USER_JTI = new ThreadLocal<>();
    // 当前登录用户名，管理端写操作日志时直接用
    private static final ThreadLocal<String> USERNAME = new ThreadLocal<>();

    /** 存入当前用户信息 */
    public static void set(Long userId, Integer role, String jti, String username) {
        USER_ID.set(userId);
        USER_ROLE.set(role);
        USER_JTI.set(jti);
        USERNAME.set(username);
    }

    /** 获取当前登录用户ID */
    public static Long getUserId() {
        return USER_ID.get();
    }

    /** 获取当前登录用户角色: 0=普通用户, 1=商家, 2=管理员 */
    public static Integer getRole() {
        return USER_ROLE.get();
    }

    /** 获取当前请求 token 的 jti */
    public static String getJti() {
        return USER_JTI.get();
    }

    /** 获取当前登录用户名 */
    public static String getUsername() {
        return USERNAME.get();
    }

    /** 请求结束时必须调用，防止内存泄漏 */
    public static void clear() {
        USER_ID.remove();
        USER_ROLE.remove();
        USER_JTI.remove();
        USERNAME.remove();
    }
}
