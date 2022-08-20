package com.microtf.framework.jpa;

import com.microtf.framework.jpa.entity.FeishuUserToken;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * 文章jpa
 *
 * @author guliuzhong
 */
public interface FeishuUserTokenRepository extends JpaRepository<FeishuUserToken, Long> {
}
