package com.microtf.framework.dto.miniapp;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public  class MiniAppLoginResponse extends MiniAppResponse{
    String jwtPayload;
    String openId;
}
