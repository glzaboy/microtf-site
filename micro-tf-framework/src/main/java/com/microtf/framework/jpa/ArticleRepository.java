package com.microtf.framework.jpa;

import com.microtf.framework.jpa.entity.ArticleEntity;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * 文章jpa
 *
 * @author guliuzhong
 */
public interface ArticleRepository extends JpaRepository<ArticleEntity, Integer> {
}
