package com.microtf.framework.dto.miniapp;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonUnwrapped;
import lombok.Data;

/**
 * 微信小程序获取用户登录信息
 * @author glzaboy@163.com
 * {
 * "openid":"xxxxxx",
 * "session_key":"xxxxx",
 * "unionid":"xxxxx",
 * "errcode":0,
 * "errmsg":"xxxxx"
 * }
 */
@Data
public class WxLogin {
    @JsonProperty(value = "openid")
    String openId;
    String session_key;
    String unionId;
    @JsonUnwrapped
    WxMessageState wxMessageState;
}
