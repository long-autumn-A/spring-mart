package com.enshi.springmart.service;

import com.enshi.springmart.common.result.PageResult;
import com.enshi.springmart.vo.AdminShopVO;
import com.enshi.springmart.vo.AdminUserVO;
import com.enshi.springmart.vo.ProductVO;

/**
 * 管理端业务逻辑：用户/店铺/商品的管理动作都收在这里，
 * 让 Controller 只负责收参数和返回结果。
 */
public interface AdminService {

    /** 分页查用户，keyword 同时匹配用户名/昵称/手机号 */
    PageResult<AdminUserVO> pageUsers(String keyword, Integer role, Integer status, int page, int pageSize);

    /** 封禁（status=1）或解封（status=0）用户 */
    void changeUserStatus(Long userId, Integer status);

    /** 分页查店铺，keyword 匹配店铺名/店主账号 */
    PageResult<AdminShopVO> pageShops(String keyword, Integer status, int page, int pageSize);

    /**
     * 封禁（status=0）或解封（status=1）店铺
     * @return 封禁时连带下架的商品数量
     */
    int changeShopStatus(Long shopId, Integer status);

    /** 分页查商品，keyword 匹配商品名 */
    PageResult<ProductVO> pageProducts(String keyword, Integer status, Long shopId, int page, int pageSize);

    /** 强制下架（status=0）或恢复上架（status=1）商品 */
    void changeProductStatus(Long productId, Integer status);
}
