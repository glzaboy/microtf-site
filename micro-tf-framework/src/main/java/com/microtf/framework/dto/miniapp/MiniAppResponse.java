package com.microtf.framework.dto.miniapp;

import lombok.Getter;
import lombok.Setter;

/**
 * 微信小程序接口返回公共内容
 *
 * @author glzaboy
 */
@Getter
@Setter
public class MiniAppResponse {
    private String errorCode;
    private String errorMsg;
}
