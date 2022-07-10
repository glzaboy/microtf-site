package com.microtf.api.test;

import com.microtf.api.Application;
import com.microtf.framework.services.SettingService;
import com.microtf.framework.services.storage.Config;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(classes = Application.class)
@Slf4j
public class SettingSettingTest {
    @Autowired
    SettingService settingService;
    @Test
    public void test(){
        Config config=new Config();
        config.setBucket("microtf");
        config.setRegion("cn-east-1");
        config.setEndPoint("http://s3-cn-east-1.qiniucs.com/");
        config.setRootPath("s3");
        config.setAccessKeyId("YktOMQv-P4IE6K7PUtCxL1vg5lBrg2N3eIefOEls");
        config.setSecretAccessKey("FhSwYnE2SyLVIQN7bssUwGokf-EhSYz_kqhn1Olt");
        config.setIsPrivate(true);
        config.setExpiry(3600);
        config.setUrlHost("http://microtf.qintingfm.com");
        settingService.saveSetting("microtf", config);
    }
    @Test
    public void test2(){
        Config microtf = settingService.getSetting("microtf", Config.class);
        log.info(String.valueOf(microtf));
        Config microtf2 = settingService.getSetting("microtf", Config.class);
        log.info(String.valueOf(microtf2));
        Config microtf3 = settingService.getSetting("microtf", Config.class);
        log.info(String.valueOf(microtf3));
        Config microtf4 = settingService.getSetting("microtf", Config.class);
        log.info(String.valueOf(microtf4));
    }
}
