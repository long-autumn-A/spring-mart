package com.enshi.springmart.controller.admin;

import com.enshi.springmart.common.result.PageResult;
import com.enshi.springmart.common.result.Result;
import com.enshi.springmart.service.AdminService;
import com.enshi.springmart.vo.AdminUserVO;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * 管理端 - 用户管理
 * 这一整个 /api/v1/admin/** 都由 AdminInterceptor 把关，只有 role=2 能进
 */
@RestController
@RequestMapping("/api/v1/admin/user")
public class AdminUserController {

    @Resource
    private AdminService adminService;

    /** 用户列表：支持按用户名/昵称/手机号搜，按角色和状态过滤 */
    @GetMapping("/list")
    public Result<PageResult<AdminUserVO>> list(@RequestParam(required = false) String keyword,
                                                @RequestParam(required = false) Integer role,
                                                @RequestParam(required = false) Integer status,
                                                @RequestParam(required = false, defaultValue = "1") int page,
                                                @RequestParam(required = false, defaultValue = "10") int pageSize) {
        return Result.success(adminService.pageUsers(keyword, role, status, page, pageSize));
    }

    /** 封禁 / 解封用户：status=1 封禁（并强制下线），status=0 解封 */
    @PutMapping("/{id}/status")
    public Result<?> changeStatus(@PathVariable Long id, @RequestParam Integer status) {
        adminService.changeUserStatus(id, status);
        return Result.success(status != null && status == 1
                ? "已封禁该用户，其登录状态已失效"
                : "已解封该用户", null);
    }
}
