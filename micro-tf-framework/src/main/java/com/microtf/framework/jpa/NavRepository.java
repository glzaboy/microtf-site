package com.microtf.framework.jpa;

import com.microtf.framework.jpa.entity.NavEntity;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * 导航jpa
 *
 * @author guliuzhong
 */
public interface NavRepository extends JpaRepository<NavEntity, Integer> {
}
