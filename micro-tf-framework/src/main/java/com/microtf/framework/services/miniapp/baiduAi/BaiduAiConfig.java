package com.microtf.framework.services.miniapp.baiduAi;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.microtf.framework.dto.SettingDto;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class BaiduAiConfig extends SettingDto {
    @JsonProperty("client_id")
    String clientId;
    @JsonProperty("client_secret")
    String clientSecret;
}
