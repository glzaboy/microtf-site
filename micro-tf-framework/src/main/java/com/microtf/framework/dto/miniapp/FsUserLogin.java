package com.microtf.framework.dto.miniapp;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class FsUserLogin {
    @JsonProperty(value = "grant_type")
    String grantType;
    String code;
    @JsonProperty(value = "refresh_token")
    String refreshToken;
}
