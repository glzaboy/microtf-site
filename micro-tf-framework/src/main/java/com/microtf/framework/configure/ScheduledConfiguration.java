package com.microtf.framework.configure;

import com.microtf.framework.services.miniapp.FsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.Scheduled;

@Configuration
public class ScheduledConfiguration {
    FsService fsService;

    @Autowired
    public void setFsService(FsService fsService) {
        this.fsService = fsService;
    }
    @Scheduled(cron = "0 0 * * * ?")//秒 分 时 日 月 年
    public void cleanUp() {
        fsService.refreshToken("cli_a2d09ef7ea38d00d");
    }

}