package com.enshi.springmart.controller;

import com.enshi.springmart.common.exception.BusinessException;
import com.enshi.springmart.common.result.Result;
import com.enshi.springmart.common.result.ResultCode;
import com.enshi.springmart.redis.CaptchaRedisService;
import com.enshi.springmart.utils.CaptchaImageUtils;
import com.enshi.springmart.vo.CaptchaVO;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

// 登录验证码：生成一张图片，答案存进 Redis，登录时拿 uuid + 用户输入去比对
@RestController
@RequestMapping("/api/v1/user")
public class CaptchaController {

    @Resource
    private CaptchaRedisService captchaRedisService;

    @GetMapping("/captcha")
    public Result<CaptchaVO> captcha(HttpServletRequest request) {
        // 同一个 IP 每分钟最多要 10 次，防止有人写脚本疯狂刷验证码
        if (!captchaRedisService.tryAcquire(getClientIp(request))) {
            throw new BusinessException(ResultCode.TOO_MANY_REQUESTS);
        }

        String uuid = UUID.randomUUID().toString().replace("-", "");
        String code = CaptchaImageUtils.randomCode();
        // 答案存 Redis，5 分钟有效，登录校验时一次性消费掉
        captchaRedisService.save(uuid, code);

        return Result.success(new CaptchaVO(uuid, CaptchaImageUtils.toBase64Png(code)));
    }

    // 取真实客户端 IP，兼容以后用 Nginx 反代的情况
    private String getClientIp(HttpServletRequest request) {
        String forwarded = request.getHeader("X-Forwarded-For");
        if (forwarded != null && !forwarded.isBlank() && !"unknown".equalsIgnoreCase(forwarded)) {
            return forwarded.split(",")[0].trim();
        }
        String realIp = request.getHeader("X-Real-IP");
        if (realIp != null && !realIp.isBlank()) {
            return realIp;
        }
        return request.getRemoteAddr();
    }
}
