package com.microtf.framework.services.storage.feishu;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;

/**
 * 飞书api 权限难入参
 *
 * @author glzaboy
 */
@Data
@Builder
public class TokenInput {

    @JsonProperty("app_id")
    private String appId;
    @JsonProperty("app_secret")
    private String appSecret;
}
