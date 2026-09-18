package com.enshi.springmart.controller.admin;

import com.enshi.springmart.common.result.PageResult;
import com.enshi.springmart.common.result.Result;
import com.enshi.springmart.service.AdminService;
import com.enshi.springmart.vo.AdminShopVO;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * 管理端 - 店铺管理
 */
@RestController
@RequestMapping("/api/v1/admin/shop")
public class AdminShopController {

    @Resource
    private AdminService adminService;

    /** 店铺列表：支持按店铺名/店主账号搜，按状态过滤 */
    @GetMapping("/list")
    public Result<PageResult<AdminShopVO>> list(@RequestParam(required = false) String keyword,
                                                @RequestParam(required = false) Integer status,
                                                @RequestParam(required = false, defaultValue = "1") int page,
                                                @RequestParam(required = false, defaultValue = "10") int pageSize) {
        return Result.success(adminService.pageShops(keyword, status, page, pageSize));
    }

    /** 封禁 / 解封店铺：status=0 封禁（连带下架该店所有在售商品），status=1 恢复营业 */
    @PutMapping("/{id}/status")
    public Result<?> changeStatus(@PathVariable Long id, @RequestParam Integer status) {
        int offShelf = adminService.changeShopStatus(id, status);
        boolean banned = status != null && status == 0;
        return Result.success(banned
                ? "已封禁该店铺，同时下架 " + offShelf + " 件在售商品"
                : "已恢复该店铺营业（商品需商家自行重新上架）", null);
    }
}
