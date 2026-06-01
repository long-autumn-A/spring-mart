package com.enshi.springmart.controller;

import com.enshi.springmart.common.exception.BusinessException;
import com.enshi.springmart.common.result.Result;
import com.enshi.springmart.common.result.ResultCode;
import com.enshi.springmart.dto.ShopSaveDTO;
import com.enshi.springmart.dto.ShopUpdateDTO;
import com.enshi.springmart.service.ShopService;
import com.enshi.springmart.vo.ShopVO;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 店铺相关接口
 */
@RestController
@RequestMapping("/api/v1/shop")
public class ShopController {

    @Autowired
    private ShopService shopService;

    // ==================== 商家接口 ====================

    /** 创建店铺（仅限商家角色） */
    @PostMapping("/create")
    public Result<ShopVO> createShop(@Valid @RequestBody ShopSaveDTO saveDTO,
                                     HttpServletRequest request) {
        Long userId = getCurrentUserId(request);
        checkMerchant(request);
        ShopVO shopVO = shopService.createShop(userId, saveDTO);
        return Result.success("店铺创建成功", shopVO);
    }

    /** 获取我自己的店铺信息 */
    @GetMapping("/my")
    public Result<ShopVO> getMyShop(HttpServletRequest request) {
        Long userId = getCurrentUserId(request);
        checkMerchant(request);
        ShopVO shopVO = shopService.getMyShop(userId);
        return Result.success(shopVO);
    }

    /** 修改我的店铺信息 */
    @PutMapping("/update")
    public Result<ShopVO> updateShop(@Valid @RequestBody ShopUpdateDTO updateDTO,
                                     HttpServletRequest request) {
        Long userId = getCurrentUserId(request);
        checkMerchant(request);
        ShopVO shopVO = shopService.updateShop(userId, updateDTO);
        return Result.success("店铺信息更新成功", shopVO);
    }

    // ==================== 公开接口 ====================

    /** 根据ID查看店铺详情 */
    @GetMapping("/detail/{id}")
    public Result<ShopVO> getShopDetail(@PathVariable Long id) {
        ShopVO shopVO = shopService.getShopById(id);
        return Result.success(shopVO);
    }

    /** 营业中店铺列表（首页用） */
    @GetMapping("/list")
    public Result<List<ShopVO>> listOpenShops() {
        List<ShopVO> list = shopService.listOpenShops();
        return Result.success(list);
    }

    // ==================== 权限和工具方法 ====================

    private void checkMerchant(HttpServletRequest request) {
        Object roleObj = request.getAttribute("currentUserRole");
        if (roleObj == null || !"1".equals(roleObj.toString())) {
            throw new BusinessException(ResultCode.FORBIDDEN.getCode(), "仅限商家操作");
        }
    }

    private Long getCurrentUserId(HttpServletRequest request) {
        Object userIdObj = request.getAttribute("currentUserId");
        if (userIdObj == null) {
            throw new BusinessException(ResultCode.UNAUTHORIZED.getCode(), "无法获取当前用户信息");
        }
        return Long.valueOf(userIdObj.toString());
    }
}
