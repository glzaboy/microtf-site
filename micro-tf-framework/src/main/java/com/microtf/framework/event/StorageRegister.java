package com.microtf.framework.event;

import com.microtf.framework.services.BeanService;
import com.microtf.framework.services.SettingService;
import com.microtf.framework.services.storage.Config;
import com.microtf.framework.services.storage.S3StorageService;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.Properties;

/**
 * 存储自动注册
 * @author glzaboy
 */
@Component
@Slf4j
public class StorageRegister implements ApplicationListener<ContextRefreshedEvent> {

    BeanService beanService;

    @Autowired
    public void setBeanService(BeanService beanService) {
        this.beanService = beanService;
    }
    SettingService settingService;

    @Autowired
    public void setSettingService(SettingService settingService) {
        this.settingService = settingService;
    }

    @Override
    public void onApplicationEvent(@NotNull ContextRefreshedEvent event) {
        log.info("注册存储事件");
        Map<String, Config> settingByClass = settingService.getSettingByClass(Config.class);
        for (Map.Entry<String,Config> item:settingByClass.entrySet()){
            log.info("注册{}",item.getKey());
            Properties properties=new Properties();
            properties.put("config",item.getValue());
            properties.put("pathStart",item.getValue().getRootPath());
            beanService.register(item.getKey(), S3StorageService.class,properties);
        }
        log.info("注册存储事件 finish");
    }
}
