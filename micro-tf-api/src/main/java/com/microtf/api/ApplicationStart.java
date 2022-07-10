package com.microtf.api;

import com.microtf.framework.services.BeanService;
import com.microtf.framework.services.SettingService;
import com.microtf.framework.services.storage.Config;
import com.microtf.framework.services.storage.S3StorageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.Properties;

/**
 * 程序启动
 * @author glzaboy
 */
@Component
public class ApplicationStart implements ApplicationRunner {

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
    public void run(ApplicationArguments args) {
        Map<String, Config> settingByClass = settingService.getSettingByClass(Config.class);
        for (Map.Entry<String,Config> item:settingByClass.entrySet()){
            Properties properties=new Properties();
            properties.put("config",item.getValue());
            properties.put("pathStart",item.getValue().getRootPath());
            beanService.register(item.getKey(), S3StorageService.class,properties);
        }
    }
}
