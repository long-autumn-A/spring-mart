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

    // 商品模块
    PRODUCT_NOT_FOUND(2001, "商品不存在"),
    STOCK_INSUFFICIENT(2002, "库存不足"),

    // 分类模块
    CATEGORY_NOT_FOUND(3001, "分类不存在"),
    CATEGORY_HAS_CHILDREN(3002, "该分类下还有子分类，无法删除"),
    CATEGORY_PARENT_NOT_FOUND(3003, "父分类不存在"),
    CATEGORY_PARENT_SELF(3004, "不能将自己设为父分类"),

    // 订单模块
    ORDER_NOT_FOUND(3001, "订单不存在"), // 注意：你原代码中分类和订单都用了3001、3002，建议后续将订单改为3500+或别的，暂时保持原样
    ORDER_CANNOT_CANCEL(3002, "订单无法取消"),

    // 店铺模块
    SHOP_NOT_FOUND(4001, "店铺不存在"),
    SHOP_ALREADY_EXISTS(4002, "您已经创建过店铺了"),
    SHOP_NAME_EXISTS(4003, "店铺名称已被占用"),

    // 购物车模块
    CART_ITEM_NOT_FOUND(5001, "购物车记录不存在或无权操作"),
    CART_PRODUCT_SKU_NOT_FOUND(5002, "添加的商品不存在"),
    CART_PRODUCT_OFF_SHELF(5003, "商品已下架，无法加入购物车"),
    CART_STOCK_INSUFFICIENT(5004, "加入购物车失败，购买总数已超过商品当前库存"),
    CART_QUANTITY_INVALID(5005, "商品数量必须大于0"),
    CART_EMPTY_CHECKOUT(5006, "未选中任何商品，无法结算"),
    CART_ITEM_LIMIT_EXCEEDED(5007, "购物车商品种类已达上限，请先清理"),
    CART_UPDATE_STOCK_INSUFFICIENT(5008, "修改数量失败，商品库存不足");

    private final int code;
    private final String message;
    }