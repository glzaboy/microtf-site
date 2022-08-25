package com.microtf.framework.jpa;

import com.microtf.framework.jpa.entity.FeishuUserToken;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * 飞书用户token
 *
 * @author glzaboy
 * @version 1.0
 */
public interface FeishuUserTokenRepository extends JpaRepository<FeishuUserToken, Long> {
}
