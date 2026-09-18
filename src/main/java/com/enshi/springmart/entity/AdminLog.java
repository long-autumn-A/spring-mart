package com.enshi.springmart.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 管理员操作日志：谁在什么时候对哪个对象做了什么。
 * 只增不改不删，所以没有逻辑删除字段。
 */
@Data
@TableName("admin_log")
public class AdminLog implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(type = IdType.AUTO)
    private Long id;

    /** 操作人：管理员用户ID */
    private Long adminId;

    /** 操作人用户名（冗余存一份，看日志时不用再联表） */
    private String adminUsername;

    /** 动作，例如：封禁用户、强制下架商品 */
    private String action;

    /** 操作对象类型: USER / SHOP / PRODUCT / CATEGORY */
    private String targetType;

    /** 操作对象ID */
    private Long targetId;

    /** 操作详情 */
    private String detail;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdAt;
}
