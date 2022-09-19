package com.microtf.framework.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 控制器登录及检测
 *
 * @author glzaboy
 */
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface Login {
    /**
     * 禁止guest 访问，
     * 当全局启用guest 时此参数才有意义
     *
     * @return 是否禁用guest
     */
    boolean disableGuest() default false;
}
