package com.microtf.framework.jpa;

import com.microtf.framework.jpa.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * 用户Jpa
 *
 * @author <a href="mailto:glzaboy@163.com">glzaboy@163.com</a>
 */
public interface UserRepository extends JpaRepository<UserEntity, Long> {
}
