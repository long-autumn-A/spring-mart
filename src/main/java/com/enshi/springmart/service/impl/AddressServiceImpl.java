package com.enshi.springmart.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.enshi.springmart.common.exception.BusinessException;
import com.enshi.springmart.common.result.ResultCode;
import com.enshi.springmart.entity.Address;
import com.enshi.springmart.mapper.AddressMapper;
import com.enshi.springmart.service.AddressService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class AddressServiceImpl extends ServiceImpl<AddressMapper, Address> implements AddressService {

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean saveAddress(Address address) {
        // 限制每个用户最多 20 个地址
        long count = this.count(new LambdaQueryWrapper<Address>()
                .eq(Address::getUserId, address.getUserId()));
        if (count >= 20) {
            throw new BusinessException(ResultCode.ADDRESS_LIMIT_EXCEEDED);
        }
        if (address.getIsDefault() == 1) {
            this.resetDefaultAddress(address.getUserId());
        }
        return this.save(address);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean updateAddress(Address address) {
        // isDefault 为可选字段，仅当明确设为 1 时才重置其他默认地址
        if (address.getIsDefault() != null && address.getIsDefault() == 1) {
            this.resetDefaultAddress(address.getUserId());
        }
        return this.update(address, new LambdaUpdateWrapper<Address>()
                .eq(Address::getId, address.getId())
                .eq(Address::getUserId, address.getUserId()));
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean setDefaultAddress(Long userId, Long addressId) {
        this.resetDefaultAddress(userId);
        Address address = new Address();
        address.setIsDefault(1);
        return this.update(address, new LambdaUpdateWrapper<Address>()
                .eq(Address::getId,addressId)
        .eq(Address::getUserId,userId));
    }

    @Override
    public List<Address> listByUserId(Long userId) {
        return this.list(new LambdaQueryWrapper<Address>()
                .eq(Address::getUserId, userId)
                .orderByDesc(Address::getIsDefault)
                .orderByDesc(Address::getUpdatedAt));
    }

    @Override
    public boolean deleteAddressSafe(Long userId, Long addressId) {
        // 构造带有 userId 校验的物理/逻辑删除
        return this.remove(new LambdaQueryWrapper<Address>()
                .eq(Address::getId, addressId)
                .eq(Address::getUserId, userId));
    }

    /**
     * 将该用户的所有地址重置为非默认
     */
    private void resetDefaultAddress(Long userId) {
        Address updateWrapper = new Address();
        updateWrapper.setIsDefault(0);

        this.update(updateWrapper, new LambdaUpdateWrapper<Address>()
                .eq(Address::getUserId, userId)
                .eq(Address::getIsDefault, 1));
    }
}
