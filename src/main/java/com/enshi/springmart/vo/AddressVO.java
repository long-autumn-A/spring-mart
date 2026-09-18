package com.enshi.springmart.vo;

import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
public class AddressVO implements Serializable {
    private Long id;
    private Long userId;
    private String receiverName;
    private String receiverPhone;
    private String province;
    private String city;
    private String district;
    private String detail;
    private Integer isDefault;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    // 额外扩展字段：方便前端直接展示完整地址，不需要前端自己拼接
    private String fullAddress;
}
