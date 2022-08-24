package com.microtf.framework.services;

import com.microtf.framework.dto.login.LoginType;
import com.microtf.framework.dto.login.LoginUser;
import com.microtf.framework.exceptions.LoginException;
import com.microtf.framework.exceptions.PasswordException;
import com.microtf.framework.utils.PasswordUtil;

import java.util.Optional;

/**
 * 用户登录接口
 * @author glzaboy
 */
public interface LoginAuth {
    /**
     * 获取用登录类型
     * @return 登录类型
     */
    default LoginType getLoginType() {
        return null;
    }

    /**
     * 登录验证
     * @param loginUser 用户信息
     * @return 用户登录信息
     * @throws LoginException 登录失败信息
     */
    default Optional<LoginUser> login(LoginUser loginUser) throws LoginException {
        throw new LoginException("不支持登录");
    }

    /**
     * 密码加密
     * @param passwd 原密码
     * @return 加密后密码
     * @author glzaboy
     */
    @SuppressWarnings("unused")
    default String encodePassword(String passwd){
        return PasswordUtil.encodePassword(passwd);
    }
    /**
     * 验证用户名和密码
     * @param password 原密码
     * @param encodePasswd 加密后的密码
     * @return true 成功 false 失败
     * @throws LoginException 密码验证异常
     */
    default Boolean validPassword(String password,String encodePasswd) throws LoginException {
        try {
            return PasswordUtil.validPassword(password,encodePasswd);
        } catch (PasswordException e) {
            throw new LoginException("验证密码失败,"+e.getMessage());
        }
    }
}
