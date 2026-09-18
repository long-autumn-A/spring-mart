package com.enshi.springmart.controller.admin;

import com.enshi.springmart.common.result.PageResult;
import com.enshi.springmart.common.result.Result;
import com.enshi.springmart.entity.AdminLog;
import com.enshi.springmart.service.AdminLogService;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * 管理端 - 操作日志（只读）
 */
@RestController
@RequestMapping("/api/v1/admin/log")
public class AdminLogController {

    @Resource
    private AdminLogService adminLogService;

    @GetMapping("/list")
    public Result<PageResult<AdminLog>> list(@RequestParam(required = false, defaultValue = "1") int page,
                                             @RequestParam(required = false, defaultValue = "10") int pageSize) {
        return Result.success(adminLogService.pageLogs(page, pageSize));
    }
}
