package com.microtf.framework.dto.miniapp;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

/**
 * 微信小程序接口返回值
 * @author guliuzhong
 */
@Data
public class WxMessageState {
    @JsonProperty(value = "errcode")
    private String errCode;
    @JsonProperty(value = "errmsg")
    private String errMsg;
}
