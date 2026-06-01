package com.enshi.springmart.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.enshi.springmart.dto.ShopSaveDTO;
import com.enshi.springmart.dto.ShopUpdateDTO;
import com.enshi.springmart.entity.Shop;
import com.enshi.springmart.vo.ShopVO;

import java.util.List;

/**
 * 店铺相关的业务逻辑接口
 */
public interface ShopService extends IService<Shop> {

    /** 创建店铺（仅限商家角色） */
    ShopVO createShop(Long userId, ShopSaveDTO saveDTO);

    /** 查自己的店铺 */
    ShopVO getMyShop(Long userId);

    /** 根据ID查店铺（公开） */
    ShopVO getShopById(Long shopId);

    /** 修改店铺信息 */
    ShopVO updateShop(Long userId, ShopUpdateDTO updateDTO);

    /** 查营业中的店铺列表（公开，首页用） */
    List<ShopVO> listOpenShops();
}
