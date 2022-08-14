package com.microtf.framework.services.miniapp;

import com.microtf.framework.dto.SettingDto;
import lombok.Getter;
import lombok.Setter;

/**
 * 微信小程序应用配置
 * @author glzaboy@163.com
 */
@Getter
@Setter
public class WxAppInfo extends SettingDto {
    /**
     * appId 应用ID
     */
    private String appId;
    /**
     * appSecret 应用密匙
     */
    private String appSecret;
    /**
     * 应用名称
     */
    private String name;
}
