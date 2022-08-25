package com.microtf.api.test;


import com.microtf.api.Application;
import com.microtf.framework.services.miniapp.BaiduAiService;
import com.microtf.framework.services.miniapp.baiduAi.OcrResult;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(classes = Application.class)
@Slf4j
public class BaiduAiTest {
    @Autowired
    BaiduAiService baiduAiService;

    @Test
    public void testPlan() {
        baiduAiService.getPlant("https://admin.microtf.com/api/wxv2/getFeishuFile/boxcnmvAP5QqxjyDLBFZaFXOgKc",5);
    }
    @Test
    public void testOcrText() {
        OcrResult picOcrText = baiduAiService.getPicOcrText("https://img2.baidu.com/it/u=3502362999,3830275433&fm=253&fmt=auto&app=138&f=JPEG?w=588&h=500");
        log.info(picOcrText.toString());
    }
}
