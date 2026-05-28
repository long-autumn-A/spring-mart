package com.enshi.springmart.vo;

import lombok.Data;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 用户信息返前端展现层对象 (VO)
 */
@Data
public class UserV0 implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 用户ID（转为String防止前端JS长整型精度丢失）
     */
    private String id;

    /**
     * 用户名
     */
    private String username;

    /**
     * 昵称
     */
    private String nickname;

    /**
     * 手机号（高安全系统可在此处自行做脱敏处理，如 138****1234）
     */
    private String phone;

    /**
     * 邮箱
     */
    private String email;

    /**
     * 头像URL
     */
    private String avatar;

    /**
     * 角色: 0=普通用户, 1=商家, 2=管理员
     */
    private Integer role;

    /**
     * 状态: 0=正常, 1=禁用
     */
    private Integer status;

    /**
     * 创建时间
     */
    private LocalDateTime createdAt;
}
