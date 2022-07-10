package com.microtf.framework.jpa;

import com.microtf.framework.jpa.entity.SettingEntity;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * 设置
 *
 * @author <a href="mailto:glzaboy@163.com">glzaboy@163.com</a>
 */
public interface SettingRepository extends JpaRepository<SettingEntity, Integer> {
}
