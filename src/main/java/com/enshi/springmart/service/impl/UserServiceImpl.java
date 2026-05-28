package com.enshi.springmart.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.enshi.springmart.dto.UserLoginDTO;
import com.enshi.springmart.dto.UserRegisterDTO;
import com.enshi.springmart.dto.UserUpdateDTO;
import com.enshi.springmart.entity.User;
import com.enshi.springmart.mapper.UserMapper;
import com.enshi.springmart.service.UserService;
import com.enshi.springmart.vo.UserV0;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {
    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public UserV0 register(UserRegisterDTO registerDTO) {
//        检查用户名是否存在
        Long count = this.lambdaQuery()
                .eq(User::getUsername, registerDTO.getUsername())
                .count();
        if (count > 0) {
            throw new RuntimeException("用户名被占用");
        }
//        检查手机号是否存在
        Long phoneCount = this.lambdaQuery()
                .eq(User::getPhone,registerDTO.getPhone())
                .count();
        if (phoneCount > 0) {
            throw new RuntimeException("该手机号已被注册");
        }
//        Dto转entity
        User user = new User();
        BeanUtils.copyProperties(registerDTO, user);
//        使用 Argon2 加密密码
        String encodedPassword = passwordEncoder.encode(registerDTO.getPassword());
        user.setPassword(encodedPassword);
//        保存到数据库
        baseMapper.insert(user);
//        返回Vo
        return convertToVO(user);
    }



    @Override
    public UserV0 login(UserLoginDTO loginDTO) {
//        根据用户名或手机号查询用户
        User user = this.lambdaQuery()
                .eq(User::getUsername, loginDTO.getAccount())
                .or()
                .eq(User::getPhone, loginDTO.getAccount())
                .one();
        if (user == null) {
            throw new RuntimeException("用户不存在或密码错误");
        }
//        状态校验
        if (user.getStatus() == 1){
            throw new RuntimeException("该账号已被禁用，请联系管理员");
        }
//        使用 Argon2 比对密码
        if (!passwordEncoder.matches(loginDTO.getPassword(), user.getPassword())) {
            throw new RuntimeException("用户不存在或密码错误");
        }
        return convertToVO(user);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public UserV0 updateUserInfo(UserUpdateDTO updateDTO) {
//        检查用户是否存在
        User user = baseMapper.selectById(updateDTO.getId());
        if (user == null) {
            throw new RuntimeException("用户不存在");
        }

//        将修改的属性赋予 Entity
        BeanUtils.copyProperties(updateDTO, user);

//         更新数据库
        baseMapper.updateById(user);

//         返回最新的用户信息
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
