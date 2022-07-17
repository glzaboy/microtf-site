package com.microtf.framework.jpa;

import com.microtf.framework.jpa.entity.CategoryEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * 类目分类
 *
 * @author guliuzhong
 */
public interface CategoryRepository extends JpaRepository<CategoryEntity, Long> {
    /**
     * 通过分类进行筛选
     * @param categoryIds 分类ID
     * @return 数据库中的分类
     */
    List<CategoryEntity> findAllByIdIn(List<Long> categoryIds);
}
