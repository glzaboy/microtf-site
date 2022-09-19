package com.microtf.framework.dto.login;

import lombok.Builder;
import lombok.Data;
import lombok.ToString;

/**
 * 登录用户状态
 *
 * @author glzaboy glzaboy@163.com
 */
@Builder
@ToString
@Data
public class LoginStateDto {
    /**
     * 登录ID
     */
    String loginId;
    /**
     * 用户ID一般为主键
     */
    String userId;
    /**
     * 登录方式
     */
    LoginType loginType;
    /**
     * 是否guest
     */
    Boolean guest;
}
