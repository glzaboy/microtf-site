package com.microtf.framework.exceptions;

/**
 * 登录失败异常
 *
 * @author glzaboy
 */
public class PasswordException extends Exception {
    public PasswordException(String message) {
        super(message);
    }
}
