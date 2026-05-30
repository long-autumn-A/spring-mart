package com.enshi.springmart.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.enshi.springmart.common.exception.BusinessException;
import com.enshi.springmart.common.result.ResultCode;
import com.enshi.springmart.common.security.JwtUtils;
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
        // 检查用户名是否存在
        Long count = this.lambdaQuery()
                .eq(User::getUsername, registerDTO.getUsername())
                .count();
        if (count > 0) {
            throw new BusinessException(ResultCode.USERNAME_EXIST);
        }
        // 检查手机号是否存在
        Long phoneCount = this.lambdaQuery()
                .eq(User::getPhone, registerDTO.getPhone())
                .count();
        if (phoneCount > 0) {
            throw new BusinessException(ResultCode.PHONE_EXIST);
        }
        // Dto转entity
        User user = new User();
        BeanUtils.copyProperties(registerDTO, user);
        // 使用 Argon2 加密密码
        String encodedPassword = passwordEncoder.encode(registerDTO.getPassword());
        user.setPassword(encodedPassword);

        user.setCreatedAt(LocalDateTime.now());
        user.setUpdatedAt(LocalDateTime.now());

        // 保存到数据库
        baseMapper.insert(user);
        log.info("新用户注册成功: username={}, id={}", user.getUsername(), user.getId());
        // 返回Vo
        return convertToVO(user);
    }


    @Override
    public LoginResultV0 login(UserLoginDTO loginDTO) {
        // 根据用户名或手机号查询用户
        User user = this.lambdaQuery()
                .eq(User::getUsername, loginDTO.getAccount())
                .or()
                .eq(User::getPhone, loginDTO.getAccount())
                .one();
        if (user == null) {
            throw new BusinessException(ResultCode.USER_NOT_FOUND);
        }
        // 状态校验
        if (user.getStatus() == 1) {
            throw new BusinessException(ResultCode.ACCOUNT_DISABLED);
        }
        // 使用 Argon2 比对密码
        if (!passwordEncoder.matches(loginDTO.getPassword(), user.getPassword())) {
            throw new BusinessException(ResultCode.PASSWORD_ERROR);
        }
        // 密码正确，组装登录结果
        UserV0 userV0 = convertToVO(user);
        // JwtUtils 生成 Token 令牌
        String token = JwtUtils.createToken(user.getId().toString(), user.getUsername());
        // 封装进统一的登录返回对象
        LoginResultV0 result = new LoginResultV0();
        result.setToken(token);
        result.setUserInfo(userV0);
        log.info("用户登录成功: username={}", user.getUsername());
        return result;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public UserV0 updateUserInfo(UserUpdateDTO updateDTO) {
        // 检查用户是否存在
        User user = baseMapper.selectById(updateDTO.getId());
        if (user == null) {
            throw new BusinessException(ResultCode.USER_NOT_FOUND);
        }

        // 将修改的属性赋予 Entity（null 值不会覆盖原有数据）
        BeanUtils.copyProperties(updateDTO, user);

        // 更新数据库
        baseMapper.updateById(user);
        log.info("用户信息更新成功: id={}", user.getId());

        // 返回最新的用户信息
        return convertToVO(baseMapper.selectById(user.getId()));
    }

    /**
     * 辅助方法：将 Entity 转换为脱敏的 VO
     */
    private UserV0 convertToVO(User user) {
        UserV0 vo = new UserV0();
        BeanUtils.copyProperties(user, vo);
        // Long 类型的 ID 转成 String 给前端，防止 JS 精度丢失
        vo.setId(user.getId().toString());
        return vo;
    }
}
