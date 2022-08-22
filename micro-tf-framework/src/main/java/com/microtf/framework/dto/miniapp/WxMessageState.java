package com.microtf.framework.dto.miniapp;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class WxMessageState {
    @JsonProperty(value = "errcode")
    private String errCode;
    @JsonProperty(value = "errmsg")
    private String errMsg;
}
