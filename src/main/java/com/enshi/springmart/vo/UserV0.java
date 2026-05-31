package com.enshi.springmart.vo;

import lombok.Data;
import java.io.Serializable;
import java.time.LocalDateTime;

// 返回给前端看的用户信息，把密码去掉了
@Data
public class UserV0 implements Serializable {

    private static final long serialVersionUID = 1L;

    // id 用 String，因为前端 JS 的 number 类型存不下太长的整数，会丢精度
    private String id;

    private String username;

    private String nickname;

    private String phone; // 之后可以考虑加个脱敏处理

    private String email;

    private String avatar;

    // 0 买家、1 商家、2 管理员
    private Integer role;

    // 0 正常、1 被禁用
    private Integer status;

    private LocalDateTime createdAt;
}
