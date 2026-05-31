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
import com.enshi.springmart.service.UserService;
import com.enshi.springmart.vo.LoginResultV0;
import com.enshi.springmart.vo.UserV0;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;


@Slf4j
@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {
    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public UserV0 register(UserRegisterDTO registerDTO) {
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
        // 用输入的内容去匹配用户名或者手机号
        User user = this.lambdaQuery()
                .eq(User::getUsername, loginDTO.getAccount())
                .or()
                .eq(User::getPhone, loginDTO.getAccount())
                .one();
        if (user == null) {
            throw new BusinessException(ResultCode.USER_NOT_FOUND);
        }
        // 如果是被禁用状态就不能登录
        if (user.getStatus() == 1) {
            throw new BusinessException(ResultCode.ACCOUNT_DISABLED);
        }
        // 拿 Argon2 比对密码，套个 try-catch 防止 Argon2 内部报错被当成系统异常
        boolean passwordMatch;
        try {
            passwordMatch = passwordEncoder.matches(loginDTO.getPassword(), user.getPassword());
        } catch (Exception e) {
            log.error("密码验证时 Argon2 炸了", e);
            throw new BusinessException(ResultCode.PASSWORD_ERROR);
        }
        if (!passwordMatch) {
            throw new BusinessException(ResultCode.PASSWORD_ERROR);
        }
        // 密码对了，生成 token 和返回信息
        UserV0 userV0 = convertToVO(user);
        String token = JwtUtils.createToken(user.getId().toString(), user.getUsername());
        LoginResultV0 result = new LoginResultV0();
        result.setToken(token);
        result.setUserInfo(userV0);
        log.info("用户登录成功: username={}", user.getUsername());
        return result;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public UserV0 updateUserInfo(UserUpdateDTO updateDTO) {
        User user = baseMapper.selectById(updateDTO.getId());
        if (user == null) {
            throw new BusinessException(ResultCode.USER_NOT_FOUND);
        }
        // 只更新前端传了的字段，null 的不会覆盖
        BeanUtils.copyProperties(updateDTO, user);
        baseMapper.updateById(user);
        log.info("用户信息更新成功: id={}", user.getId());
        return convertToVO(baseMapper.selectById(user.getId()));
    }

    @Override
    public UserV0 getUserById(Long userId) {
        User user = baseMapper.selectById(userId);
        if (user == null) {
            throw new BusinessException(ResultCode.USER_NOT_FOUND);
        }
        return convertToVO(user);
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
