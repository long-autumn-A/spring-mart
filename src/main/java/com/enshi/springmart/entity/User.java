package com.enshi.springmart.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 用户表，存所有用户的信息，买家和卖家都在这里面
 */
@Data
@TableName("user")
public class User implements Serializable {
    private static final long serialVersionUID = 1L;

    @TableId(type = IdType.AUTO)
    private Long id;

    // 用户名，注册的时候自己起的
    private String username;

    // 密码，存的是 Argon2 加密之后的密文
    private String password;

    // 昵称，展示给其他人看的
    private String nickname;

    // 手机号，也可以用来登录
    private String phone;

    // 邮箱，可以不填
    private String email;

    // 头像地址
    private String avatar;

    // 0 是买家、1 是商家、2 是管理员
    private Integer role;

    // 0 是正常、1 是被封禁了
    private Integer status;

    // 注册时间
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdAt;

    // 最近一次修改信息的时间
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updatedAt;

    // 逻辑删除，0 还在，1 已经删了
    @TableLogic
    private Integer deleted;
}
