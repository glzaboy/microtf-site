package com.microtf.framework.services.miniapp;

import com.microtf.framework.dto.miniapp.WxLogin;
import com.microtf.framework.exceptions.BizException;
import com.microtf.framework.services.SettingService;
import com.microtf.framework.utils.HttpUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class WxService {

    SettingService settingService;
    @Autowired
    public void setSettingService(SettingService settingService) {
        this.settingService = settingService;
    }
    public String LOGIN_URL="https://api.weixin.qq.com/sns/jscode2session?&js_code=JSCODE&grant_type=authorization_code";

    public WxLogin login(String appId, String code) {
        WxAppInfo setting = settingService.getSetting(appId, WxAppInfo.class);
        if(!setting.getRead()){
            throw new BizException("没有读取到配置");
        }
        HttpUtil.HttpRequest.HttpRequestBuilder builder=HttpUtil.HttpRequest.builder();
        builder.url(LOGIN_URL);
        Map<String,String> opt=new HashMap<>();
        opt.put("appid",appId);
        opt.put("secret",setting.getAppSecret());
        opt.put("js_code",code);
        builder.query(opt);
        HttpUtil.HttpResponse sent = HttpUtil.sent(builder.build());
        WxLogin json = sent.json(WxLogin.class);
        return json;
    }
}
