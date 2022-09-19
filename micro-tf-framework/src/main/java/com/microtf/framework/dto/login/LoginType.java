package com.microtf.framework.dto.login;

/**
 * 用户登录方式
 *
 * @author glzaboy
 */
public enum LoginType {
    /**
     * 用户名密码
     */
    PASSWORD("password"),
    /**
     * 微信的appId
     */
    WECHAT("wechat");

    LoginType(String loginType) {
    }
}
