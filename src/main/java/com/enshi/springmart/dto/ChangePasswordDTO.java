package com.enshi.springmart.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.io.Serializable;

// 修改密码的时候传来的，要填旧密码和新密码
@Data
public class ChangePasswordDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    @NotBlank(message = "旧密码不能为空")
    private String oldPassword;

    @NotBlank(message = "新密码不能为空")
    @Size(min = 6, max = 32, message = "新密码长度必须在 6-32 个字符之间")
    private String newPassword;
}
