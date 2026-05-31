package com.enshi.springmart.dto;

import lombok.Data;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import java.io.Serializable;

// 修改个人资料的时候前端传过来的
@Data
public class UserUpdateDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    @NotBlank(message = "用户ID不能为空")
    private String id; // 用 String 是因为 JS 的 number 存不下 Java 的 Long，会丢精度

    private String nickname;

    @Pattern(regexp = "^1[3-9]\\d{9}$", message = "手机号格式不正确")
    private String phone;

    @Email(message = "邮箱格式不正确")
    private String email;

    private String avatar;
}
