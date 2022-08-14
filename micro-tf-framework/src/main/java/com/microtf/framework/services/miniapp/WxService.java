package com.microtf.framework.services.miniapp;

import com.microtf.framework.services.SettingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class WxService {

    SettingService settingService;
    @Autowired
    public void setSettingService(SettingService settingService) {
        this.settingService = settingService;
    }
    public String LOGIN_URL="https://api.weixin.qq.com/sns/jscode2session?&js_code=JSCODE&grant_type=authorization_code";

    public String login(String appId, String code) {
        WxAppInfo setting = settingService.getSetting(appId, WxAppInfo.class);
        if(!setting.getRead()){
            return "没有读取到配置";
        }
        return null;
//        Map<String,String> opt=new HashMap<>();
//        opt.put("appid",appId);
//        opt.put("secret",setting.getAppSecret());
//        opt.put("js_code",code);
//        OkHttpClient.Builder builder=new OkHttpClient.Builder();
//        OkHttpClient build = builder.build();
//        Request request=new Request();
//        Response execute = build.newCall(request).execute();
//        HttpUtil.HttpMethod.values();
//        ResponseEntity<WxLogin> forEntity = restTemplate.getForEntity(LOGIN_URL, WxLogin.class, opt);
//        if(forEntity.getStatusCode().is2xxSuccessful()){
//            return forEntity.getBody().getOpenId();
//        }else{
//            return null;
//        }
    }
}
