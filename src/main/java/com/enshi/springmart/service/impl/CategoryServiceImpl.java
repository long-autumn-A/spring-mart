package com.enshi.springmart.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.enshi.springmart.common.exception.BusinessException;
import com.enshi.springmart.common.result.ResultCode;
import com.enshi.springmart.dto.CategorySaveDTO;
import com.enshi.springmart.dto.CategoryUpdateDTO;
import com.enshi.springmart.entity.Category;
import com.enshi.springmart.mapper.CategoryMapper;
import com.enshi.springmart.service.CategoryService;
import com.enshi.springmart.vo.CategoryTreeVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Service
public class CategoryServiceImpl extends ServiceImpl<CategoryMapper, Category> implements CategoryService {

    @Override
    public boolean saveCategory(CategorySaveDTO saveDTO) {
        // 如果指定了父分类，校验父分类是否存在
        if (saveDTO.getParentId() != null && saveDTO.getParentId() != 0) {
            Category parent = baseMapper.selectById(saveDTO.getParentId());
            if (parent == null) {
                throw new BusinessException(ResultCode.CATEGORY_PARENT_NOT_FOUND);
            }
        }
        Category category = new Category();
        BeanUtils.copyProperties(saveDTO, category);
        int rows = baseMapper.insert(category);
        log.info("分类新增成功: name={}, id={}, parentId={}", category.getName(), category.getId(), category.getParentId());
        return rows > 0;
    }

    @Override
    public boolean deleteCategory(Long id) {
        Category category = baseMapper.selectById(id);
        if (category == null) {
            throw new BusinessException(ResultCode.CATEGORY_NOT_FOUND);
        }
        // 有子分类的不让删
        Long childCount = this.lambdaQuery()
                .eq(Category::getParentId, id)
                .count();
        if (childCount > 0) {
            throw new BusinessException(ResultCode.CATEGORY_HAS_CHILDREN);
        }
        // TODO: 后续关联 goods 表后，校验该分类下是否有商品，有则不允许删除
        int rows = baseMapper.deleteById(id);
        log.info("分类删除成功: id={}, name={}", id, category.getName());
        return rows > 0;
    }

    @Override
    public boolean updateCategory(CategoryUpdateDTO updateDTO) {
        Category category = baseMapper.selectById(updateDTO.getId());
        if (category == null) {
            throw new BusinessException(ResultCode.CATEGORY_NOT_FOUND);
        }
        // 不能把自己设成自己的父分类
        if (updateDTO.getParentId() != null && updateDTO.getParentId().equals(updateDTO.getId())) {
            throw new BusinessException(ResultCode.CATEGORY_PARENT_SELF);
        }
        // 如果改了父分类，校验新父分类是否存在
        if (updateDTO.getParentId() != null && updateDTO.getParentId() != 0) {
            Category parent = baseMapper.selectById(updateDTO.getParentId());
            if (parent == null) {
                throw new BusinessException(ResultCode.CATEGORY_PARENT_NOT_FOUND);
            }
        }
        BeanUtils.copyProperties(updateDTO, category);
        int rows = baseMapper.updateById(category);
        log.info("分类更新成功: id={}, name={}", category.getId(), category.getName());
        return rows > 0;
    }

    @Override
    public List<CategoryTreeVO> getCategoryTree() {
        // 查全量分类，按 sort_order 升序
        List<Category> allCategories = this.lambdaQuery()
                .orderByAsc(Category::getSortOrder)
                .list();

        // 实体 → VO
        List<CategoryTreeVO> allVos = allCategories.stream().map(cat -> {
            CategoryTreeVO vo = new CategoryTreeVO();
            BeanUtils.copyProperties(cat, vo);
            return vo;
        }).toList();

        // 按 parentId 分组
        Map<Long, List<CategoryTreeVO>> groupedByParent = allVos.stream()
                .collect(Collectors.groupingBy(CategoryTreeVO::getParentId));

        // 递归组装树：从根节点（parentId==0）开始，给每个节点塞 children
        List<CategoryTreeVO> roots = groupedByParent.getOrDefault(0L, new ArrayList<>());
        for (CategoryTreeVO root : roots) {
            attachChildren(root, groupedByParent);
        }
        return roots;
    }

    @Override
    public List<CategoryTreeVO> getFlatCategoryList() {
        // 只查顶层分类（parentId == 0），按 sort_order 升序
        List<Category> topCategories = this.lambdaQuery()
                .eq(Category::getParentId, 0L)
                .orderByAsc(Category::getSortOrder)
                .list();

        return topCategories.stream().map(cat -> {
            CategoryTreeVO vo = new CategoryTreeVO();
            BeanUtils.copyProperties(cat, vo);
            return vo;
        }).toList();
    }

    private void attachChildren(CategoryTreeVO parent, Map<Long, List<CategoryTreeVO>> groupedByParent) {
        List<CategoryTreeVO> children = groupedByParent.get(parent.getId());
        if (children != null && !children.isEmpty()) {
            parent.setChildren(children);
            for (CategoryTreeVO child : children) {
                attachChildren(child, groupedByParent);
            }
        }
    }
}
