package com.microtf.framework.jpa;

import com.microtf.framework.jpa.entity.LoginEntity;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * 用户登录
 *
 * @author <a href="mailto:glzaboy@163.com">glzaboy@163.com</a>
 */
public interface LoginRepository extends JpaRepository<LoginEntity, Integer> {
}
