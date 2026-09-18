package com.enshi.springmart.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.enshi.springmart.common.exception.BusinessException;
import com.enshi.springmart.common.result.PageResult;
import com.enshi.springmart.common.result.ResultCode;
import com.enshi.springmart.entity.Product;
import com.enshi.springmart.entity.Shop;
import com.enshi.springmart.entity.User;
import com.enshi.springmart.redis.TokenRedisService;
import com.enshi.springmart.redis.UserCacheService;
import com.enshi.springmart.service.AdminLogService;
import com.enshi.springmart.service.AdminService;
import com.enshi.springmart.service.ProductService;
import com.enshi.springmart.service.ShopService;
import com.enshi.springmart.service.UserService;
import com.enshi.springmart.utils.UserContext;
import com.enshi.springmart.vo.AdminShopVO;
import com.enshi.springmart.vo.AdminUserVO;
import com.enshi.springmart.vo.ProductVO;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@Slf4j
@Service
public class AdminServiceImpl implements AdminService {

    @Resource
    private UserService userService;

    @Resource
    private ShopService shopService;

    @Resource
    private ProductService productService;

    @Resource
    private TokenRedisService tokenRedisService;

    @Resource
    private UserCacheService userCacheService;

    @Resource
    private AdminLogService adminLogService;

    // ==================== 用户管理 ====================

    @Override
    public PageResult<AdminUserVO> pageUsers(String keyword, Integer role, Integer status, int page, int pageSize) {
        Page<User> p = new Page<>(page, pageSize);
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
        if (StringUtils.hasText(keyword)) {
            String kw = keyword.trim();
            wrapper.and(w -> w.like(User::getUsername, kw)
                    .or().like(User::getNickname, kw)
                    .or().like(User::getPhone, kw));
        }
        if (role != null) {
            wrapper.eq(User::getRole, role);
        }
        if (status != null) {
            wrapper.eq(User::getStatus, status);
        }
        wrapper.orderByDesc(User::getId);
        userService.page(p, wrapper);

        List<User> users = p.getRecords();
        Map<Long, Shop> shopMap = loadShopsOfMerchants(users);

        List<AdminUserVO> records = users.stream().map(user -> {
            AdminUserVO vo = new AdminUserVO();
            BeanUtils.copyProperties(user, vo);
            vo.setId(user.getId().toString());
            Shop shop = shopMap.get(user.getId());
            if (shop != null) {
                vo.setShopId(shop.getId());
                vo.setShopName(shop.getName());
                vo.setShopStatus(shop.getStatus());
            }
            return vo;
        }).toList();

        return new PageResult<>(records, p.getTotal(), p.getCurrent(), p.getSize());
    }

    /** 把这一页里商家的店铺一次性查出来，避免逐行查库 */
    private Map<Long, Shop> loadShopsOfMerchants(List<User> users) {
        List<Long> merchantIds = users.stream()
                .filter(u -> u.getRole() != null && u.getRole() == 1)
                .map(User::getId)
                .toList();
        if (merchantIds.isEmpty()) {
            return Map.of();
        }
        Map<Long, Shop> shopMap = new HashMap<>();
        shopService.lambdaQuery().in(Shop::getUserId, merchantIds).list()
                .forEach(shop -> shopMap.put(shop.getUserId(), shop));
        return shopMap;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void changeUserStatus(Long userId, Integer status) {
        checkStatus(status, "用户");
        User user = userService.getById(userId);
        if (user == null) {
            throw new BusinessException(ResultCode.USER_NOT_FOUND);
        }
        if (user.getRole() != null && user.getRole() == 2) {
            throw new BusinessException(ResultCode.FORBIDDEN.getCode(), "不能封禁管理员账号");
        }
        if (userId.equals(UserContext.getUserId())) {
            throw new BusinessException(ResultCode.FORBIDDEN.getCode(), "不能封禁自己的账号");
        }

        boolean banned = status == 1;
        user.setStatus(status);
        userService.updateById(user);
        // 缓存里存的是旧状态，不清掉的话前端还会看到"正常"
        userCacheService.delete(userId);

        StringBuilder detail = new StringBuilder("账号: ").append(user.getUsername());
        if (banned) {
            // 改数据库只能挡住下次登录，已经在线的会话必须删白名单才会立刻失效
            tokenRedisService.removeAllTokens(userId);
            // 商家被封，连带把店铺关掉、商品下架，否则前台还看得到一家"死店"
            if (user.getRole() != null && user.getRole() == 1) {
                Shop shop = shopService.lambdaQuery().eq(Shop::getUserId, userId).one();
                if (shop != null) {
                    int offShelf = closeShopAndOffShelfProducts(shop);
                    detail.append("；连带关闭店铺[").append(shop.getName())
                            .append("]，下架商品 ").append(offShelf).append(" 件");
                }
            }
        }
        adminLogService.record(banned ? "封禁用户" : "解封用户", "USER", userId, detail.toString());
    }

    // ==================== 店铺管理 ====================

    @Override
    public PageResult<AdminShopVO> pageShops(String keyword, Integer status, int page, int pageSize) {
        Page<Shop> p = new Page<>(page, pageSize);
        LambdaQueryWrapper<Shop> wrapper = new LambdaQueryWrapper<>();
        if (StringUtils.hasText(keyword)) {
            String kw = keyword.trim();
            // 店主账号也支持搜，先按用户名把店主的ID捞出来
            List<Long> ownerIds = userService.lambdaQuery().like(User::getUsername, kw).list()
                    .stream().map(User::getId).toList();
            wrapper.and(w -> {
                w.like(Shop::getName, kw).or().like(Shop::getPhone, kw);
                if (!ownerIds.isEmpty()) {
                    w.or().in(Shop::getUserId, ownerIds);
                }
            });
        }
        if (status != null) {
            wrapper.eq(Shop::getStatus, status);
        }
        wrapper.orderByDesc(Shop::getId);
        shopService.page(p, wrapper);

        List<Shop> shops = p.getRecords();
        Map<Long, User> ownerMap = loadOwners(shops);

        List<AdminShopVO> records = shops.stream().map(shop -> {
            AdminShopVO vo = new AdminShopVO();
            BeanUtils.copyProperties(shop, vo);
            User owner = ownerMap.get(shop.getUserId());
            if (owner != null) {
                vo.setOwnerUsername(owner.getUsername());
                vo.setOwnerStatus(owner.getStatus());
            }
            return vo;
        }).toList();

        return new PageResult<>(records, p.getTotal(), p.getCurrent(), p.getSize());
    }

    private Map<Long, User> loadOwners(List<Shop> shops) {
        List<Long> ownerIds = shops.stream()
                .map(Shop::getUserId)
                .filter(Objects::nonNull)
                .distinct()
                .toList();
        if (ownerIds.isEmpty()) {
            return Map.of();
        }
        Map<Long, User> ownerMap = new HashMap<>();
        userService.listByIds(ownerIds).forEach(user -> ownerMap.put(user.getId(), user));
        return ownerMap;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int changeShopStatus(Long shopId, Integer status) {
        checkStatus(status, "店铺");
        Shop shop = shopService.getById(shopId);
        if (shop == null) {
            throw new BusinessException(ResultCode.SHOP_NOT_FOUND);
        }

        boolean banned = status == 0;
        int offShelf = 0;
        if (banned) {
            offShelf = closeShopAndOffShelfProducts(shop);
        } else {
            shop.setStatus(status);
            shopService.updateById(shop);
        }

        adminLogService.record(banned ? "封禁店铺" : "解封店铺", "SHOP", shopId,
                "店铺: " + shop.getName()
                        + (banned ? "；连带下架商品 " + offShelf + " 件" : "（商品需商家自行重新上架）"));
        return offShelf;
    }

    /** 关店并把该店铺在售商品全部下架 */
    private int closeShopAndOffShelfProducts(Shop shop) {
        shop.setStatus(0);
        shopService.updateById(shop);
        return offShelfProductsOfShop(shop.getId());
    }

    /** 把某店铺还在售的商品全部下架，返回受影响条数 */
    private int offShelfProductsOfShop(Long shopId) {
        Product update = new Product();
        update.setStatus(0);
        return productService.getBaseMapper().update(update, new LambdaUpdateWrapper<Product>()
                .eq(Product::getShopId, shopId)
                .eq(Product::getStatus, 1));
    }

    // ==================== 商品管理 ====================

    @Override
    public PageResult<ProductVO> pageProducts(String keyword, Integer status, Long shopId, int page, int pageSize) {
        Page<Product> p = new Page<>(page, pageSize);
        LambdaQueryWrapper<Product> wrapper = new LambdaQueryWrapper<>();
        if (StringUtils.hasText(keyword)) {
            wrapper.like(Product::getName, keyword.trim());
        }
        if (status != null) {
            wrapper.eq(Product::getStatus, status);
        }
        if (shopId != null) {
            wrapper.eq(Product::getShopId, shopId);
        }
        wrapper.orderByDesc(Product::getId);
        productService.page(p, wrapper);

        List<Product> products = p.getRecords();
        Map<Long, String> shopNames = loadShopNames(products);

        List<ProductVO> records = products.stream().map(product -> {
            ProductVO vo = new ProductVO();
            BeanUtils.copyProperties(product, vo);
            vo.setShopName(shopNames.get(product.getShopId()));
            return vo;
        }).toList();

        return new PageResult<>(records, p.getTotal(), p.getCurrent(), p.getSize());
    }

    private Map<Long, String> loadShopNames(List<Product> products) {
        List<Long> shopIds = products.stream()
                .map(Product::getShopId)
                .filter(Objects::nonNull)
                .distinct()
                .toList();
        if (shopIds.isEmpty()) {
            return Map.of();
        }
        Map<Long, String> names = new HashMap<>();
        shopService.listByIds(shopIds).forEach(shop -> names.put(shop.getId(), shop.getName()));
        return names;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void changeProductStatus(Long productId, Integer status) {
        checkStatus(status, "商品");
        Product product = productService.getById(productId);
        if (product == null) {
            throw new BusinessException(ResultCode.PRODUCT_NOT_FOUND);
        }
        // 复用商家那套状态变更逻辑，管理员不受"只能改自己商品"的限制
        productService.updateStatus(productId, status);
        adminLogService.record(status == 0 ? "强制下架商品" : "恢复上架商品", "PRODUCT", productId,
                "商品: " + product.getName());
    }

    // ==================== 公共校验 ====================

    private void checkStatus(Integer status, String what) {
        if (status == null || (status != 0 && status != 1)) {
            throw new BusinessException(ResultCode.BAD_REQUEST.getCode(), what + "的状态只能是 0 或 1");
        }
    }
}
