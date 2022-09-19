package com.microtf.framework.services.storage.feishu;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

/**
 * 飞书接口 token
 *
 * @author guliuzhong
 */
@Data
public class Token {


    @JsonProperty("code")
    private Integer code;
    @JsonProperty("msg")
    private String msg;
    @JsonProperty("tenant_access_token")
    private String tenantAccessToken;
    @JsonProperty("expire")
    private Integer expire;
}
