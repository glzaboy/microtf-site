package com.microtf.framework.services.miniapp;

import com.microtf.framework.exceptions.BizException;
import com.microtf.framework.services.SettingService;
import com.microtf.framework.services.miniapp.baiduAi.*;
import com.microtf.framework.utils.HttpUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.net.HttpURLConnection;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;

/**
 * 百度Ai接口封装
 * @author glzaboy@163.com
 */
@Service
public class BaiduAiService {
    private SettingService settingService;

    @Autowired
    public void setSettingService(SettingService settingService) {
        this.settingService = settingService;
    }

    RedisTemplate<String, HttpUtil.HttpAuthReturn> redisTemplate;

    @Resource
    public void setRedisTemplate(RedisTemplate<String, HttpUtil.HttpAuthReturn> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    public static final String AI_PLANT_URL = "https://aip.baidubce.com/rest/2.0/image-classify/v1/plant";
    public static final String AI_OCR_URL = "https://aip.baidubce.com/rest/2.0/ocr/v1/general";
    public final Function<HttpUtil.HttpAuth, HttpUtil.HttpAuthReturn> httpBearValue = (HttpUtil.HttpAuth httpAuth) -> {
        HttpUtil.HttpAuthReturn httpAuthReturn = redisTemplate.opsForValue().get("baiduAi" + httpAuth.getUser() + httpAuth.getPwd());
        if (httpAuthReturn != null) {
            return httpAuthReturn;
        }
        HttpUtil.HttpAuthReturn httpAuthReturn1 = new HttpUtil.HttpAuthReturn();
        HttpUtil.HttpRequest.HttpRequestBuilder builder = HttpUtil.HttpRequest.builder();
        builder.url("https://aip.baidubce.com/oauth/2.0/token");
        builder.method(HttpUtil.Method.FORM).form(HttpUtil.object2Map(TokenInput.builder().clientId(httpAuth.getUser()).clientSecret(httpAuth.getPwd()).grantType("client_credentials").build()));
        HttpUtil.HttpResponse sent = HttpUtil.sent(builder.build());
        if (sent.getStatus() == HttpURLConnection.HTTP_OK) {
            Token json = sent.json(Token.class);
            if (json.getError() != null) {
                throw new BizException(json.getErrorDescription());
            }
            httpAuthReturn1.setAuthValue(json.getAccessToken());
            httpAuthReturn1.setRequestParamName("access_token");
            redisTemplate.opsForValue().set(httpAuth.getUser() + httpAuth.getPwd(), httpAuthReturn1, json.getExpiresIn() - 1800, TimeUnit.SECONDS);
            return httpAuthReturn1;
        } else {
            return null;
        }
    };

    public PlantResult getPlant(String pictureUrl, Integer retInfoNum) {
        BaiduAiConfig baiduAi = settingService.getSetting("baiduAi", BaiduAiConfig.class);
        if (!baiduAi.getRead()) {
            throw new BizException("ai接口没有设置");
        }

        HttpUtil.HttpRequest.HttpRequestBuilder builder = HttpUtil.HttpRequest.builder();
        builder.method(HttpUtil.Method.FORM);
        Map<String, String> post = new HashMap<>(16);
        post.put("baike_num", retInfoNum.toString());
        post.put("url", pictureUrl);
        builder.authFunction(httpBearValue);
        builder.auth(HttpUtil.HttpAuth.builder().user(baiduAi.getClientId()).pwd(baiduAi.getClientSecret()).build());
        HttpUtil.HttpResponse sent = HttpUtil.sent(builder.url(AI_PLANT_URL).form(post).build());
        if (sent.getStatus() != HttpURLConnection.HTTP_OK) {
            throw new BizException("接口出错");
        }
        return sent.json(PlantResult.class);
    }

    public OcrResult getPicOcrText(String pictureUrl) {
        BaiduAiConfig baiduAi = settingService.getSetting("baiduAi", BaiduAiConfig.class);
        if (!baiduAi.getRead()) {
            throw new BizException("ai接口没有设置");
        }

        HttpUtil.HttpRequest.HttpRequestBuilder builder = HttpUtil.HttpRequest.builder();
        builder.method(HttpUtil.Method.FORM);
        Map<String, String> post = new HashMap<>(16);
        post.put("detect_direction", "true");
        post.put("paragraph", "true");
        post.put("url", pictureUrl);
        builder.authFunction(httpBearValue);
        builder.auth(HttpUtil.HttpAuth.builder().user(baiduAi.getClientId()).pwd(baiduAi.getClientSecret()).build());
        HttpUtil.HttpResponse sent = HttpUtil.sent(builder.url(AI_OCR_URL).form(post).build());
        if (sent.getStatus() != HttpURLConnection.HTTP_OK) {
            throw new BizException("接口出错");
        }
        OcrResult json = sent.json(OcrResult.class);
        if (json.getErrorCode() != null) {
            throw new BizException(json.getErrorMsg());
        }
        return json;
    }
}
