package com.microtf.framework.dto.miniapp;

import com.microtf.framework.dto.SettingDto;
import lombok.Getter;
import lombok.Setter;

/**
 * 微信小程序应用配置
 * @author glzaboy@163.com
 * {
 * "openid":"xxxxxx",
 * "session_key":"xxxxx",
 * "unionid":"xxxxx",
 * "errcode":0,
 * "errmsg":"xxxxx"
 * }
 */
@Getter
@Setter
public class WxLogin extends WxBaseResponse{
    String openId;
    String session_key;
    String unionId;

}
