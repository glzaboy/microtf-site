package com.microtf.api.test;


import com.microtf.api.Application;
import com.microtf.framework.services.miniapp.BaiduAiService;
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
        baiduAiService.getPlant("http://microtf.com/api/wxv2/getFeishuFile/boxcnmvAP5QqxjyDLBFZaFXOgKc",5);
    }
}
