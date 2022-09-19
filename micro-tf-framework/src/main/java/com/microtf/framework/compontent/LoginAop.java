package com.microtf.framework.compontent;

import com.microtf.framework.annotations.Login;
import com.microtf.framework.dto.login.LoginStateDto;
import com.microtf.framework.exceptions.LoginException;
import com.microtf.framework.services.LoginService;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.stereotype.Component;

/**
 * 用户登录aop
 *
 * @author glzaboy
 */
@Component
@Aspect
public class LoginAop {
    LoginService loginService;

    @Autowired
    public void setLoginService(LoginService loginService) {
        this.loginService = loginService;
    }

    /**
     * 用户登录信息初始化
     * 本程序生效范围使用@Login注释
     *
     * @param joinPoint 执行切入点
     * @return 执行结果
     * @throws Throwable 初始化失败时出错异常
     * @see Login
     */
    @Around("@annotation(com.microtf.framework.annotations.Login)")
    public Object loginCheck(ProceedingJoinPoint joinPoint) throws Throwable {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        loginService.initLoginUser();
        LoginStateDto loginStateDto = loginService.getLoginStateDto();
        if (loginStateDto.getGuest()) {
            if (!loginService.getEnableGuest()) {
                throw new LoginException("用户未登录或已过期，请重新登录");
            } else {
                Login annotation = signature.getMethod().getAnnotation(Login.class);
                Login annotation1 = AnnotationUtils.getAnnotation(annotation, Login.class);
                if (annotation1 != null && annotation1.disableGuest()) {
                    throw new LoginException("用户未登录或已过期，请重新登录");
                }
            }
        }
        Object proceed = joinPoint.proceed();
        loginService.removeLoginUser();
        return proceed;
    }
}
