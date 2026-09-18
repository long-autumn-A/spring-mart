package com.enshi.springmart.config;

import com.enshi.springmart.entity.User;
import com.enshi.springmart.service.UserService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

/**
 * 启动时保证系统里至少有一个管理员账号，否则管理端永远进不去。
 * 规则：
 *   1. 已经有 role=2 的账号 → 什么都不做
 *   2. 配置的用户名已存在但不是管理员 → 提升为管理员（密码沿用原密码，不动）
 *   3. 都不存在 → 按配置创建默认管理员
 * 账号密码都在 application.yaml 的 admin.default 下，可用环境变量覆盖。
 */
@Slf4j
@Component
public class AdminAccountInitializer implements ApplicationRunner {

    @Resource
    private UserService userService;

    @Resource
    private PasswordEncoder passwordEncoder;

    @Value("${admin.default.username:platform_admin}")
    private String username;

    @Value("${admin.default.password:}")
    private String password;

    @Value("${admin.default.nickname:平台管理员}")
    private String nickname;

    @Override
    public void run(ApplicationArguments args) {
        if (!StringUtils.hasText(username) || !StringUtils.hasText(password)) {
            log.warn("未配置 admin.default.username / password，跳过默认管理员初始化");
            return;
        }

        Long adminCount = userService.lambdaQuery().eq(User::getRole, 2).count();
        if (adminCount != null && adminCount > 0) {
            log.info("系统中已有 {} 个管理员账号，跳过默认管理员初始化", adminCount);
            return;
        }

        User exist = userService.lambdaQuery().eq(User::getUsername, username).one();
        if (exist != null) {
            exist.setRole(2);
            userService.updateById(exist);
            log.warn("已把现有账号 [{}] 提升为管理员（密码沿用原密码）", username);
            return;
        }

        User admin = new User();
        admin.setUsername(username);
        admin.setNickname(nickname);
        admin.setPassword(passwordEncoder.encode(password));
        admin.setRole(2);
        admin.setStatus(0);
        userService.save(admin);
        log.warn("已创建默认管理员账号 [{}]，密码取自 admin.default.password 配置，请登录后尽快修改", username);
    }
}
