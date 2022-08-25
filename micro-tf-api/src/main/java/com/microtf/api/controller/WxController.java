package com.microtf.api.controller;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.microtf.framework.dto.miniapp.*;
import com.microtf.framework.dto.storage.StorageObject;
import com.microtf.framework.dto.storage.StorageObjectStream;
import com.microtf.framework.exceptions.BizException;
import com.microtf.framework.services.miniapp.BaiduAiService;
import com.microtf.framework.services.miniapp.FsService;
import com.microtf.framework.services.miniapp.WxService;
import com.microtf.framework.services.miniapp.baiduAi.OcrResult;
import com.microtf.framework.services.miniapp.baiduAi.PlantResult;
import com.microtf.framework.services.storage.StorageManagerService;
import com.microtf.framework.services.storage.StorageService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;
import java.util.Optional;

/**
 * 微信小程序接口
 * @author  glzaboy
 */
@RestController
@RequestMapping("wxv2")
@Slf4j
public class WxController {
    WxService wxService;

    @Autowired
    public void setWxService(WxService wxService) {
        this.wxService = wxService;
    }

    FsService fsService;

    @Autowired
    public void setFsService(FsService fsService) {
        this.fsService = fsService;
    }
    private StorageManagerService storageManagerService;

    @Autowired
    public void setStorageManagerService(StorageManagerService storageManagerService) {
        this.storageManagerService = storageManagerService;
    }
    private BaiduAiService baiduAiService;

    @Autowired
    public void setBaiduAiService(BaiduAiService baiduAiService) {
        this.baiduAiService = baiduAiService;
    }

    @RequestMapping("login")
    public MiniAppLoginResponse login(@RequestParam String code, @RequestParam String appId) {
        MiniAppLoginResponse miniAppLoginResponse=new MiniAppLoginResponse();
        try{
            WxLogin login = wxService.login(appId, code);
            miniAppLoginResponse.setOpenId(login.getOpenId());
            Date expTime = new Date(System.currentTimeMillis() + 86400*1000L);
            String sign = JWT.create().withClaim("openId", login.getOpenId()).withClaim("appId", appId).withExpiresAt(expTime).sign(Algorithm.HMAC256("123456"));
            miniAppLoginResponse.setJwtPayload(sign);
            miniAppLoginResponse.setOpenId(login.getOpenId());
        }catch (BizException e){
            miniAppLoginResponse.setErrorCode("1");
            miniAppLoginResponse.setErrorMsg(e.getMessage());
        }
        return miniAppLoginResponse;
    }
    @RequestMapping("feishulogin/{appId}")
    public String feiShuLogin(@PathVariable String appId,@RequestParam(required = false) String code) {
        if(code==null){
            return "请使用浏览器访问 https://open.feishu.cn/open-apis/authen/v1/user_auth_page_beta?app_id=cli_a2d09ef7ea38d00d&redirect_uri=http://localhost:8080/api/wxv2/feishulogin/cli_a2d09ef7ea38d00d&state=RANDOMSTATE";
        }
        try {
            fsService.login(appId, code);
        }catch (BizException e){
            return "系统出错原因"+e.getMessage();
        }
        return "null";
    }
    @RequestMapping("getFeishuFile/{fileToken}")
    @ResponseBody
    public void getFeiShuFile(@PathVariable String fileToken, HttpServletResponse httpServletResponse) {
        try {
            Optional<StorageService> storageService = storageManagerService.selectStorage("feishu/");
            if(storageService.isPresent()){
                StorageService storageService1 = storageService.get();
                StorageObjectStream stream = storageService1.getStream(fileToken);
                for(Map.Entry<String,String> item:stream.getMetaData().entrySet()){
                    httpServletResponse.addHeader(item.getKey(),item.getValue());
                }
                httpServletResponse.getOutputStream().write(stream.getBufferedInputStream().readAllBytes());
            }else{
                httpServletResponse.getOutputStream().write("不支持此文件".getBytes(StandardCharsets.UTF_8));
            }
        }catch (BizException | IOException e){
            log.error("系统出错原因"+e.getMessage());
        }
    }
    @PostMapping("/upload")
    public UploadResponse upload(MultipartFile upload) {
        SimpleDateFormat simpleDateFormat=new SimpleDateFormat("yyyy-MM-dd");
        String store="feishu";
        Optional<StorageService> storageService = storageManagerService.selectStorage(store);
        StringBuilder fileNameBuffer= new StringBuilder();
        fileNameBuffer.append(store).append("/");
        fileNameBuffer.append(simpleDateFormat.format(new Date())).append("/").append(upload.getOriginalFilename());
        UploadResponse uploadResponse=new UploadResponse();
        if(storageService.isPresent()){
            try {
                StorageObject upload2 = storageService.get().upload(upload.getInputStream(), fileNameBuffer.toString(),upload.getContentType());
                uploadResponse.setUrl(upload2.getUrl());
                uploadResponse.setId(upload2.getObjectId());
                return uploadResponse;
            } catch (IOException e) {
                log.error("上传出错");
                uploadResponse.setErrorCode("1");
                uploadResponse.setErrorMsg("上传出错"+e.getMessage());
            }
        }else {
            uploadResponse.setErrorCode("1");
            uploadResponse.setErrorMsg("上传出错");
        }
        return uploadResponse;
    }
    @GetMapping("/getPlant")
    public MiniPlantResponse getPlant(@RequestParam String picture) {
        MiniPlantResponse response=new MiniPlantResponse();
        try{
            PlantResult plant = baiduAiService.getPlant(picture, 5);
            BeanUtils.copyProperties(plant,response);
        }catch (BizException e){
            response.setErrorCode("1");
            response.setErrorMsg("识别出错原因"+e.getMessage());
        }
        return response;
    }
    @GetMapping("/getPictureText")
    public MiniOcrResponse getPictureText(@RequestParam String picture) {
        MiniOcrResponse response=new MiniOcrResponse();
        try{
            OcrResult picOcrText = baiduAiService.getPicOcrText(picture);
            BeanUtils.copyProperties(picOcrText,response);
        }catch (BizException e){
            response.setErrorCode("1");
            response.setErrorMsg("识别出错原因"+e.getMessage());
        }
        return response;
    }
}
