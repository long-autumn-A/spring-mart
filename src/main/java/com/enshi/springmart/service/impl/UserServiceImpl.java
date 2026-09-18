package com.enshi.springmart.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.enshi.springmart.common.exception.BusinessException;
import com.enshi.springmart.common.result.ResultCode;
import com.enshi.springmart.common.security.JwtUtils;
import com.enshi.springmart.dto.ChangePasswordDTO;
import com.enshi.springmart.dto.UserLoginDTO;
import com.enshi.springmart.dto.UserRegisterDTO;
import com.enshi.springmart.dto.UserUpdateDTO;
import com.enshi.springmart.entity.User;
import com.enshi.springmart.mapper.UserMapper;
import com.enshi.springmart.redis.CaptchaRedisService;
import com.enshi.springmart.redis.LoginFailRedisService;
import com.enshi.springmart.redis.TokenRedisService;
import com.enshi.springmart.redis.UserCacheService;
import com.enshi.springmart.service.UserService;
import com.enshi.springmart.vo.LoginResultV0;
import com.enshi.springmart.vo.UserV0;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;


@Slf4j
@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {
    @Autowired
    private PasswordEncoder passwordEncoder;

    @Resource
    private CaptchaRedisService captchaRedisService;

    @Resource
    private LoginFailRedisService loginFailRedisService;

    @Resource
    private TokenRedisService tokenRedisService;

    @Resource
    private UserCacheService userCacheService;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public UserV0 register(UserRegisterDTO registerDTO) {
        // 注册同样要过验证码，不然可以拿脚本无限刷注册
        if (!captchaRedisService.validate(registerDTO.getUuid(), registerDTO.getCode())) {
            throw new BusinessException(ResultCode.VERIFICATION_ERROR);
        }

        // 先看看用户名有没有人用了
        Long count = this.lambdaQuery()
                .eq(User::getUsername, registerDTO.getUsername())
                .count();
        if (count > 0) {
            throw new BusinessException(ResultCode.USERNAME_EXIST);
        }
        // 再看看手机号是不是已经注册过了
        Long phoneCount = this.lambdaQuery()
                .eq(User::getPhone, registerDTO.getPhone())
                .count();
        if (phoneCount > 0) {
            throw new BusinessException(ResultCode.PHONE_EXIST);
        }
        // 把 DTO 转成 Entity，准备入库
        User user = new User();
        BeanUtils.copyProperties(registerDTO, user);
        // 角色只允许买家(0)或商家(1)，防止前端自选管理员
        Integer regRole = registerDTO.getRole();
        user.setRole(regRole != null && regRole == 1 ? 1 : 0);
        // 密码用 Argon2 加密再存
        String encodedPassword = passwordEncoder.encode(registerDTO.getPassword());
        user.setPassword(encodedPassword);
        user.setCreatedAt(LocalDateTime.now());
        user.setUpdatedAt(LocalDateTime.now());

        baseMapper.insert(user);
        log.info("新用户注册成功: username={}, id={}", user.getUsername(), user.getId());
        return convertToVO(user);
    }


    @Override
    public LoginResultV0 login(UserLoginDTO loginDTO) {
        String account = loginDTO.getAccount();

        // 1. 先校验验证码（一次性，无论对错都从 Redis 里删掉，防止同一个验证码反复刷接口）
        if (!captchaRedisService.validate(loginDTO.getUuid(), loginDTO.getCode())) {
            throw new BusinessException(ResultCode.VERIFICATION_ERROR);
        }

        // 2. 这个账号是不是已经因为连续输错密码被锁了
        if (loginFailRedisService.isLocked(account)) {
            throw new BusinessException(ResultCode.ACCOUNT_LOCKED.getCode(),
                    loginFailRedisService.getLockedMessage(loginFailRedisService.getRemainLockSeconds(account)));
        }

        // 3. 用输入的内容去匹配用户名或者手机号
        User user = this.lambdaQuery()
                .eq(User::getUsername, loginDTO.getAccount())
                .or()
                .eq(User::getPhone, loginDTO.getAccount())
                .one();
        if (user == null) {
            // 账号不存在也算一次失败，避免用枚举的方式猜有哪些账号
            throw loginFailException(account, ResultCode.USER_NOT_FOUND);
        }
        // 如果是被禁用状态就不能登录
        if (user.getStatus() == 1) {
            throw new BusinessException(ResultCode.ACCOUNT_DISABLED);
        }
        // 4. 拿 Argon2 比对密码，套个 try-catch 防止 Argon2 内部报错被当成系统异常
        boolean passwordMatch;
        try {
            passwordMatch = passwordEncoder.matches(loginDTO.getPassword(), user.getPassword());
        } catch (Exception e) {
            log.error("密码验证时 Argon2 炸了", e);
            throw new BusinessException(ResultCode.PASSWORD_ERROR);
        }
        if (!passwordMatch) {
            throw loginFailException(account, ResultCode.PASSWORD_ERROR);
        }

        // 5. 登录成功，把之前的失败计数清掉
        loginFailRedisService.clear(account);

        // 6. 密码对了，生成 token 和返回信息
        UserV0 userV0 = convertToVO(user);
        String token = JwtUtils.createToken(user.getId().toString(), user.getUsername(), user.getRole().toString());

        // 7. 把这条会话写进 Redis 白名单，过期时间跟 JWT 的真实剩余有效期保持一致
        //    有了这条记录，退出登录 / 改密码踢人才能立刻生效
        Map<String, String> session = new HashMap<>();
        session.put("userId", user.getId().toString());
        session.put("username", user.getUsername());
        session.put("role", user.getRole().toString());
        session.put("loginTime", LocalDateTime.now().toString());
        tokenRedisService.saveToken(
                JwtUtils.getJti(token),
                user.getId(),
                session,
                Duration.ofMillis(JwtUtils.getRemainMillis(token))
        );

        LoginResultV0 result = new LoginResultV0();
        result.setToken(token);
        result.setUserInfo(userV0);
        log.info("用户登录成功: username={}", user.getUsername());
        return result;
    }

    // 登录失败统一处理：累计一次失败次数，够次数就返回锁定异常，否则返回原本的错误原因
    private BusinessException loginFailException(String account, ResultCode resultCode) {
        loginFailRedisService.recordFail(account);
        if (loginFailRedisService.isLocked(account)) {
            return new BusinessException(ResultCode.ACCOUNT_LOCKED.getCode(),
                    loginFailRedisService.getLockedMessage(loginFailRedisService.getRemainLockSeconds(account)));
        }
        return new BusinessException(resultCode);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public UserV0 updateUserInfo(Long userId, UserUpdateDTO updateDTO) {
        User user = baseMapper.selectById(userId);
        if (user == null) {
            throw new BusinessException(ResultCode.USER_NOT_FOUND);
        }
        // 只更新前端传了的字段，null 的不会覆盖
        BeanUtils.copyProperties(updateDTO, user);
        baseMapper.updateById(user);
        // 资料改了，缓存里的旧信息必须删掉，否则下次读到的还是旧昵称/旧手机号
        userCacheService.delete(userId);
        log.info("用户信息更新成功: id={}", user.getId());
        return convertToVO(baseMapper.selectById(user.getId()));
    }

    @Override
    public UserV0 getUserById(Long userId) {
        // 先看缓存，命中就直接返回，没命中再查库并顺手写回缓存
        UserV0 cached = userCacheService.get(userId);
        if (cached != null) {
            return cached;
        }

        User user = baseMapper.selectById(userId);
        if (user == null) {
            throw new BusinessException(ResultCode.USER_NOT_FOUND);
        }
        UserV0 userV0 = convertToVO(user);
        userCacheService.save(userId, userV0);
        return userV0;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void changePassword(Long userId, ChangePasswordDTO dto) {
        User user = baseMapper.selectById(userId);
        if (user == null) {
            throw new BusinessException(ResultCode.USER_NOT_FOUND);
        }
        // 先验证旧密码对不对
        boolean oldPasswordMatch;
        try {
            oldPasswordMatch = passwordEncoder.matches(dto.getOldPassword(), user.getPassword());
        } catch (Exception e) {
            log.error("旧密码验证时 Argon2 炸了", e);
            throw new BusinessException(ResultCode.PASSWORD_ERROR);
        }
        if (!oldPasswordMatch) {
            throw new BusinessException(ResultCode.OLD_PASSWORD_ERROR);
        }
        // 加密新密码然后更新
        user.setPassword(passwordEncoder.encode(dto.getNewPassword()));
        baseMapper.updateById(user);
        // 密码都换了，之前的 token 就不该再能用：把该用户所有会话从白名单里删掉，强制重新登录
        tokenRedisService.removeAllTokens(userId);
        log.info("用户密码修改成功: id={}", userId);
    }

    // 把 Entity 转成 VO，去掉敏感字段，id 转成 String 防止前端精度丢失
    private UserV0 convertToVO(User user) {
        UserV0 vo = new UserV0();
        BeanUtils.copyProperties(user, vo);
        vo.setId(user.getId().toString());
        return vo;
    }
}
