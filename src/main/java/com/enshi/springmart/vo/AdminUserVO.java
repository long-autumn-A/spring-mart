package com.enshi.springmart.vo;

import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 管理端用户列表里的一行。
 * id 用 String 和 UserV0 保持一致，避免前端数字精度问题。
 */
@Data
public class AdminUserVO implements Serializable {

    private static final long serialVersionUID = 1L;

    private String id;

    private String username;

    private String nickname;

    private String phone;

    private String email;

    /** 0 买家、1 商家、2 管理员 */
    private Integer role;

    /** 0 正常、1 禁用 */
    private Integer status;

    private LocalDateTime createdAt;

    /** 商家才有：他的店铺信息，方便管理员确认封的是哪家店 */
    private Long shopId;

    private String shopName;

    /** 店铺状态: 0=关闭, 1=营业中 */
    private Integer shopStatus;
}
