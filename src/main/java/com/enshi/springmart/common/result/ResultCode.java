package com.enshi.springmart.common.result;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 统一响应状态码
 */
@Getter
@AllArgsConstructor
public enum ResultCode {

    SUCCESS(200, "操作成功"),
    BAD_REQUEST(400, "请求参数错误"),
    UNAUTHORIZED(401, "未登录或Token已过期"),
    FORBIDDEN(403, "无权限访问"),
    NOT_FOUND(404, "资源不存在"),
    INTERNAL_ERROR(500, "服务器内部错误"),

    // 业务状态码
    USER_NOT_FOUND(1001, "用户不存在"),
    PASSWORD_ERROR(1002, "密码错误"),
    ACCOUNT_DISABLED(1003, "账号已被禁用"),
    PHONE_EXIST(1004, "手机号已注册"),
    STOCK_INSUFFICIENT(2001, "库存不足"),
    ORDER_NOT_FOUND(2002, "订单不存在"),
    ORDER_CANNOT_CANCEL(2003, "订单无法取消");

    private final int code;
    private final String message;
}
