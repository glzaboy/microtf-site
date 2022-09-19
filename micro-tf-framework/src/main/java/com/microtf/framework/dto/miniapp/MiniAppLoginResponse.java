package com.microtf.framework.dto.miniapp;

import lombok.Getter;
import lombok.Setter;

/**
 * 微信小程序登录返回
 *
 * @author glzaboy
 */
@Getter
@Setter
public class MiniAppLoginResponse extends MiniAppResponse {
    String jwtPayload;
    String openId;
}
