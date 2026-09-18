package com.enshi.springmart.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.enshi.springmart.entity.Address;

import java.util.List;

public interface AddressService extends IService<Address> {
    boolean saveAddress(Address address);

    boolean updateAddress(Address address);

    boolean setDefaultAddress(Long userId, Long addressId);

    List<Address> listByUserId(Long userId);

    // 安全删除（带用户隔离校验）
    boolean deleteAddressSafe(Long userId, Long addressId);

}
