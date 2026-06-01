package com.enshi.springmart.common.result;

import lombok.AllArgsConstructor;
import lombok.Getter;

// 把常用的状态码和提示信息统一放在这里，免得到处写魔法数字
@Getter
@AllArgsConstructor
public enum ResultCode {

    SUCCESS(200, "操作成功"),
    BAD_REQUEST(400, "请求参数错误"),
    UNAUTHORIZED(401, "未登录或Token已过期"),
    FORBIDDEN(403, "无权限访问"),
    NOT_FOUND(404, "资源不存在"),
    INTERNAL_ERROR(500, "服务器内部错误"),

    // 用户模块的业务错误码
    USER_NOT_FOUND(1001, "用户不存在"),
    PASSWORD_ERROR(1002, "密码错误"),
    OLD_PASSWORD_ERROR(1003, "旧密码不正确"),
    ACCOUNT_DISABLED(1004, "账号已被禁用"),
    PHONE_EXIST(1005, "手机号已注册"),
    USERNAME_EXIST(1006, "用户名已被占用"),

    // 分类模块
    CATEGORY_NOT_FOUND(3001, "分类不存在"),
    CATEGORY_HAS_CHILDREN(3002, "该分类下还有子分类，无法删除"),
    CATEGORY_PARENT_NOT_FOUND(3003, "父分类不存在"),
    CATEGORY_PARENT_SELF(3004, "不能将自己设为父分类"),

    // 商品和订单相关的（先占个位）
    STOCK_INSUFFICIENT(2001, "库存不足"),
    ORDER_NOT_FOUND(2002, "订单不存在"),
    ORDER_CANNOT_CANCEL(2003, "订单无法取消");

    private final int code;
    private final String message;
}
