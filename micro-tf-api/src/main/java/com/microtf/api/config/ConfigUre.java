package com.microtf.api.config;

import com.microtf.framework.services.storage.Config;
import com.microtf.framework.services.storage.S3StorageService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ConfigUre {
    @Bean
    public S3StorageService getS3StorageService() {
        Config config=new Config();
        config.setBucket("microtf");
        config.setRegion("cn-east-1");
        config.setEndPoint("http://s3-cn-east-1.qiniucs.com/");
        config.setRootPath("/s3");
        config.setAccessKeyId("YktOMQv-P4IE6K7PUtCxL1vg5lBrg2N3eIefOEls");
        config.setSecretAccessKey("FhSwYnE2SyLVIQN7bssUwGokf-EhSYz_kqhn1Olt");
        S3StorageService s3StorageService=new S3StorageService();
        s3StorageService.setConfig(config);
        s3StorageService.setPathStart(config.getRootPath());
        return s3StorageService;
    }
}
