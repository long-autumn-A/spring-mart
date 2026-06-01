package com.enshi.springmart.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.enshi.springmart.common.exception.BusinessException;
import com.enshi.springmart.common.result.ResultCode;
import com.enshi.springmart.dto.ShopSaveDTO;
import com.enshi.springmart.dto.ShopUpdateDTO;
import com.enshi.springmart.entity.Shop;
import com.enshi.springmart.mapper.ShopMapper;
import com.enshi.springmart.service.ShopService;
import com.enshi.springmart.vo.ShopVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
public class ShopServiceImpl extends ServiceImpl<ShopMapper, Shop> implements ShopService {

    @Override
    @Transactional(rollbackFor = Exception.class)
    public ShopVO createShop(Long userId, ShopSaveDTO saveDTO) {
        // 一个用户只能开一家店
        Long count = this.lambdaQuery()
                .eq(Shop::getUserId, userId)
                .count();
        if (count > 0) {
            throw new BusinessException(ResultCode.SHOP_ALREADY_EXISTS);
        }
        // 店铺名也不能跟别人重复
        Long nameCount = this.lambdaQuery()
                .eq(Shop::getName, saveDTO.getName())
                .count();
        if (nameCount > 0) {
            throw new BusinessException(ResultCode.SHOP_NAME_EXISTS);
        }

        Shop shop = new Shop();
        BeanUtils.copyProperties(saveDTO, shop);
        shop.setUserId(userId);
        shop.setStatus(1); // 默认营业中
        baseMapper.insert(shop);
        log.info("店铺创建成功: name={}, userId={}, shopId={}", shop.getName(), userId, shop.getId());
        return convertToVO(shop);
    }

    @Override
    public ShopVO getMyShop(Long userId) {
        Shop shop = this.lambdaQuery()
                .eq(Shop::getUserId, userId)
                .one();
        if (shop == null) {
            throw new BusinessException(ResultCode.SHOP_NOT_FOUND);
        }
        return convertToVO(shop);
    }

    @Override
    public ShopVO getShopById(Long shopId) {
        Shop shop = baseMapper.selectById(shopId);
        if (shop == null) {
            throw new BusinessException(ResultCode.SHOP_NOT_FOUND);
        }
        return convertToVO(shop);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public ShopVO updateShop(Long userId, ShopUpdateDTO updateDTO) {
        Shop shop = baseMapper.selectById(updateDTO.getId());
        if (shop == null) {
            throw new BusinessException(ResultCode.SHOP_NOT_FOUND);
        }
        // 只能改自己的店
        if (!shop.getUserId().equals(userId)) {
            throw new BusinessException(ResultCode.FORBIDDEN);
        }
        // 如果改了店铺名，校验有没有跟别人重名
        if (updateDTO.getName() != null && !updateDTO.getName().equals(shop.getName())) {
            Long nameCount = this.lambdaQuery()
                    .eq(Shop::getName, updateDTO.getName())
                    .ne(Shop::getId, shop.getId())
                    .count();
            if (nameCount > 0) {
                throw new BusinessException(ResultCode.SHOP_NAME_EXISTS);
            }
        }
        BeanUtils.copyProperties(updateDTO, shop);
        baseMapper.updateById(shop);
        log.info("店铺信息更新成功: shopId={}", shop.getId());
        return convertToVO(baseMapper.selectById(shop.getId()));
    }

    @Override
    public List<ShopVO> listOpenShops() {
        List<Shop> shops = this.lambdaQuery()
                .eq(Shop::getStatus, 1)
                .orderByDesc(Shop::getRating)
                .list();
        return shops.stream().map(this::convertToVO).toList();
    }

    private ShopVO convertToVO(Shop shop) {
        ShopVO vo = new ShopVO();
        BeanUtils.copyProperties(shop, vo);
        return vo;
    }
}
