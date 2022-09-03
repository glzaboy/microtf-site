package com.microtf.framework.services.miniapp;

import com.microtf.framework.dto.miniapp.FsUserLogin;
import com.microtf.framework.dto.miniapp.FsUserLoginResponse;
import com.microtf.framework.exceptions.BizException;
import com.microtf.framework.jpa.FeishuUserTokenRepository;
import com.microtf.framework.jpa.entity.FeishuUserToken;
import com.microtf.framework.services.SettingService;
import com.microtf.framework.services.storage.feishu.Token;
import com.microtf.framework.services.storage.feishu.TokenInput;
import com.microtf.framework.utils.HttpUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.stereotype.Service;

import java.net.HttpURLConnection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;

/**
 * 飞n收服务封装
 * @author glzaboy@163.com
 */
@Service
public class FsService {

    SettingService settingService;

    @Autowired
    public void setSettingService(SettingService settingService) {
        this.settingService = settingService;
    }

    FeishuUserTokenRepository feishuUserTokenRepository;

    @Autowired
    public void setFeishuUserTokenRepository(FeishuUserTokenRepository feishuUserTokenRepository) {
        this.feishuUserTokenRepository = feishuUserTokenRepository;
    }

    public final String LOGIN_URL = "https://open.feishu.cn/open-apis/authen/v1/access_token";
    public final String REFRESH_URL = "https://open.feishu.cn/open-apis/authen/v1/refresh_access_token";
    public final Function<HttpUtil.HttpAuth, HttpUtil.HttpAuthReturn> httpBearValue = (HttpUtil.HttpAuth httpAuth) -> {
        HttpUtil.HttpAuthReturn httpAuthReturn1 = new HttpUtil.HttpAuthReturn();
        HttpUtil.HttpRequest.HttpRequestBuilder builder = HttpUtil.HttpRequest.builder();
        builder.url("https://open.feishu.cn/open-apis/auth/v3/tenant_access_token/internal");
        builder.method(HttpUtil.Method.JSON).postObject(TokenInput.builder().appId(httpAuth.getUser()).appSecret(httpAuth.getPwd()).build());
        HttpUtil.HttpResponse sent = HttpUtil.sent(builder.build());
        if (sent.getStatus() == HttpURLConnection.HTTP_OK) {
            Token json = sent.json(Token.class);
            if (json.getCode() == 0) {
                httpAuthReturn1.setAuthValue("Bearer " + json.getTenantAccessToken());
                return httpAuthReturn1;
            }
        }
        return null;
    };

    public void refreshToken(String appId) {
        WxAppInfo setting = settingService.getSetting(appId, WxAppInfo.class);
        if (!setting.getRead()) {
            throw new BizException("没有读取到配置");
        }
        FeishuUserToken feishuUserToken = new FeishuUserToken();
        feishuUserToken.setAppId(appId);
        List<FeishuUserToken> all = feishuUserTokenRepository.findAll(Example.of(feishuUserToken));
        for (FeishuUserToken item : all) {
            HttpUtil.HttpRequest.HttpRequestBuilder builder = HttpUtil.HttpRequest.builder();
            builder.url(REFRESH_URL);
            builder.method(HttpUtil.Method.JSON);
            FsUserLogin fsUserLogin = new FsUserLogin();
            fsUserLogin.setRefreshToken(item.getRefreshToken());
            fsUserLogin.setGrantType("refresh_token");
            builder.postObject(fsUserLogin);
            builder.auth(HttpUtil.HttpAuth.builder().user(setting.getAppId()).pwd(setting.getAppSecret()).build());
            builder.authFunction(httpBearValue);
            HttpUtil.HttpResponse sent = HttpUtil.sent(builder.build());
            FsUserLoginResponse json = sent.json(FsUserLoginResponse.class);
            item.setRefreshToken(json.getData().getRefreshToken());
            item.setAccessToken(json.getData().getAccessToken());
            feishuUserTokenRepository.save(item);
        }
    }

    public void login(String appId, String code) {
        WxAppInfo setting = settingService.getSetting(appId, WxAppInfo.class);
        if (!setting.getRead()) {
            throw new BizException("没有读取到配置");
        }
        HttpUtil.HttpRequest.HttpRequestBuilder builder = HttpUtil.HttpRequest.builder();
        builder.url(LOGIN_URL);
        builder.method(HttpUtil.Method.JSON);
        FsUserLogin fsUserLogin = new FsUserLogin();
        fsUserLogin.setCode(code);
        fsUserLogin.setGrantType("authorization_code");
        Map<String, String> opt = new HashMap<>(16);
        builder.postObject(fsUserLogin);
        builder.query(opt);
        builder.auth(HttpUtil.HttpAuth.builder().user(setting.getAppId()).pwd(setting.getAppSecret()).build());
        builder.authFunction(httpBearValue);
        HttpUtil.HttpResponse sent = HttpUtil.sent(builder.build());
        FsUserLoginResponse json = sent.json(FsUserLoginResponse.class);
        FeishuUserToken feishuUserToken = new FeishuUserToken();
        feishuUserToken.setOpenId(json.getData().getOpenId());
        feishuUserToken.setAppId(appId);
        Optional<FeishuUserToken> one = feishuUserTokenRepository.findOne(Example.of(feishuUserToken));
        FeishuUserToken feishuUserToken2 = one.orElseGet(FeishuUserToken::new);
        feishuUserToken2.setAccessToken(json.getData().getAccessToken());
        feishuUserToken2.setOpenId(json.getData().getOpenId());
        feishuUserToken2.setAppId(appId);
        feishuUserToken2.setRefreshToken(json.getData().getRefreshToken());
        feishuUserTokenRepository.save(feishuUserToken2);
    }
}
