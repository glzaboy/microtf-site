package com.microtf.framework.exceptions;

/**
 * 登录失败异常
 *
 * @author glzaboy
 */
public class LoginException extends Exception {
    public LoginException(String message) {
        super(message);
    }
}
