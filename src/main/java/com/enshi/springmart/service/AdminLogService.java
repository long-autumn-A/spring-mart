package com.enshi.springmart.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.enshi.springmart.common.result.PageResult;
import com.enshi.springmart.entity.AdminLog;

/**
 * 管理员操作日志
 */
public interface AdminLogService extends IService<AdminLog> {

    /**
     * 记一条管理员操作日志（操作人从 UserContext 里取）
     * 记日志失败只打日志，不影响真正的管理操作
     */
    void record(String action, String targetType, Long targetId, String detail);

    /** 分页查询操作日志，最新的在前 */
    PageResult<AdminLog> pageLogs(int page, int pageSize);
}
