package com.microtf.framework.dto.miniapp;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

/**
 * 飞书用户登录入参
 *
 * @author glzaboy
 */
@Data
public class FsUserLogin {
    @JsonProperty(value = "grant_type")
    String grantType;
    String code;
    @JsonProperty(value = "refresh_token")
    String refreshToken;
}
