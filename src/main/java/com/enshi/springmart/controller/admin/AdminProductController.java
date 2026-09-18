package com.enshi.springmart.controller.admin;

import com.enshi.springmart.common.result.PageResult;
import com.enshi.springmart.common.result.Result;
import com.enshi.springmart.service.AdminService;
import com.enshi.springmart.vo.ProductVO;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * 管理端 - 商品管理（违规商品强制下架）
 */
@RestController
@RequestMapping("/api/v1/admin/product")
public class AdminProductController {

    @Resource
    private AdminService adminService;

    /** 商品列表：支持按商品名搜，按状态、店铺过滤，默认连下架的一起看 */
    @GetMapping("/list")
    public Result<PageResult<ProductVO>> list(@RequestParam(required = false) String keyword,
                                              @RequestParam(required = false) Integer status,
                                              @RequestParam(required = false) Long shopId,
                                              @RequestParam(required = false, defaultValue = "1") int page,
                                              @RequestParam(required = false, defaultValue = "10") int pageSize) {
        return Result.success(adminService.pageProducts(keyword, status, shopId, page, pageSize));
    }

    /** 强制下架（status=0）/ 恢复上架（status=1） */
    @PutMapping("/{id}/status")
    public Result<?> changeStatus(@PathVariable Long id, @RequestParam Integer status) {
        adminService.changeProductStatus(id, status);
        return Result.success(status != null && status == 0 ? "已强制下架该商品" : "已恢复上架该商品", null);
    }
}
