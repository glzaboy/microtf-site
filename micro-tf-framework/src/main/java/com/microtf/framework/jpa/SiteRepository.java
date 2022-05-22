package com.microtf.framework.jpa;

import com.microtf.framework.jpa.entity.SiteEntity;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * 站点设置
 *
 * @author <a href="mailto:glzaboy@163.com">glzaboy@163.com</a>
 */
public interface SiteRepository extends JpaRepository<SiteEntity, Long> {
}
