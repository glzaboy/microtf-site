package com.microtf.api.controller;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.microtf.framework.dto.miniapp.MiniAppLoginResponse;
import com.microtf.framework.dto.miniapp.WxLogin;
import com.microtf.framework.services.miniapp.WxService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("wxv2")
@Slf4j
public class WxController {
    WxService wxService;

    @Autowired
    public void setWxService(WxService wxService) {
        this.wxService = wxService;
    }

    @RequestMapping("login")
    public MiniAppLoginResponse login(@RequestParam String code, @RequestParam String appId) {
        WxLogin login = wxService.login(appId, code);
        MiniAppLoginResponse miniAppLoginResponse=new MiniAppLoginResponse();
        miniAppLoginResponse.setOpenId(login.getOpenId());
        String sign = JWT.create().withClaim("openId", login.getOpenId()).withClaim("appId", appId).sign(Algorithm.HMAC256("123456"));
        miniAppLoginResponse.setJwtPayload(sign);
        miniAppLoginResponse.setOpenId(login.getOpenId());
        return miniAppLoginResponse;
    }
}
