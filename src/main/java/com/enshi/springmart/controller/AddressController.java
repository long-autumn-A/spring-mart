package com.enshi.springmart.controller;

import com.enshi.springmart.common.result.Result;
import com.enshi.springmart.dto.AddressSaveDTO;
import com.enshi.springmart.dto.AddressUpdateDTO;
import com.enshi.springmart.entity.Address;
import com.enshi.springmart.service.AddressService;
import com.enshi.springmart.utils.UserContext;
import com.enshi.springmart.vo.AddressVO;
import jakarta.validation.Valid;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("api/v1/address")
public class AddressController {
    @Autowired
    private AddressService addressService;

    // 1. 新增地址
    @PostMapping
    public Result<?> addAddress(@Valid @RequestBody AddressSaveDTO saveDTO) {
        Address address = new Address();
        BeanUtils.copyProperties(saveDTO, address);

        // 从 ThreadLocal 中获取通过 Token 解析出的真实 userId
        Long userId = UserContext.getUserId();
        address.setUserId(userId);

        if (address.getIsDefault() == null) {
            address.setIsDefault(0); // 默认不作为默认地址
        }

        addressService.saveAddress(address);
        return Result.success("添加成功", null);
    }

    // 2. 修改地址
    @PutMapping
    public Result<?> updateAddress(@Valid @RequestBody AddressUpdateDTO updateDTO) {
        Address address = new Address();
        BeanUtils.copyProperties(updateDTO, address);

        // 同样注入当前的 userId，防止越权修改别人的地址
        Long userId = UserContext.getUserId();
        address.setUserId(userId);

        addressService.updateAddress(address);
        return Result.success("修改成功", null);
    }

    // 3. 查询当前登录用户的所有地址（不再从路径/参数传 userId）
    @GetMapping
    public Result<List<AddressVO>> getAddresses() {
        Long userId = UserContext.getUserId();
        List<Address> entityList = addressService.listByUserId(userId);

        List<AddressVO> voList = entityList.stream().map(entity -> {
            AddressVO vo = new AddressVO();
            BeanUtils.copyProperties(entity, vo);
            vo.setFullAddress(entity.getProvince() + entity.getCity() + entity.getDistrict() + entity.getDetail());
            return vo;
        }).collect(Collectors.toList());

        return Result.success(voList);
    }

    // 4. 将指定地址设为默认地址
    @PutMapping("/{id}/default")
    public Result<?> setDefault(@PathVariable Long id) {
        Long userId = UserContext.getUserId();
        addressService.setDefaultAddress(userId, id);
        return Result.success("设置默认地址成功", null);
    }

    // 5. 逻辑删除地址
    @DeleteMapping("/{id}")
    public Result<?> deleteAddress(@PathVariable Long id) {
        Long userId = UserContext.getUserId();
        // 为了防止纵向越权（即恶意删除他人的地址），删除时应该校验该地址是否属于当前用户
        addressService.deleteAddressSafe(userId, id);
        return Result.success("删除成功", null);
    }
}
