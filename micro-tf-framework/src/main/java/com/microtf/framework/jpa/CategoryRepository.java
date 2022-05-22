package com.microtf.framework.jpa;

import com.microtf.framework.jpa.entity.CategoryEntity;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * 类目分类
 *
 * @author guliuzhong
 */
public interface CategoryRepository extends JpaRepository<CategoryEntity, Integer> {
}
