package com.enshi.springmart.service.impl;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.enshi.springmart.common.result.PageResult;
import com.enshi.springmart.entity.AdminLog;
import com.enshi.springmart.mapper.AdminLogMapper;
import com.enshi.springmart.service.AdminLogService;
import com.enshi.springmart.utils.UserContext;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class AdminLogServiceImpl extends ServiceImpl<AdminLogMapper, AdminLog> implements AdminLogService {

    @Override
    public void record(String action, String targetType, Long targetId, String detail) {
        try {
            AdminLog entity = new AdminLog();
            entity.setAdminId(UserContext.getUserId());
            entity.setAdminUsername(UserContext.getUsername());
            entity.setAction(action);
            entity.setTargetType(targetType);
            entity.setTargetId(targetId);
            entity.setDetail(detail);
            this.save(entity);
        } catch (Exception e) {
            // 日志只是留痕，写失败不能把管理员的操作也带崩
            log.warn("写管理员操作日志失败: action={}, targetType={}, err={}", action, targetType, e.getMessage());
        }
    }

    @Override
    public PageResult<AdminLog> pageLogs(int page, int pageSize) {
        Page<AdminLog> p = new Page<>(page, pageSize);
        this.lambdaQuery().orderByDesc(AdminLog::getId).page(p);
        return new PageResult<>(p.getRecords(), p.getTotal(), p.getCurrent(), p.getSize());
    }
}
