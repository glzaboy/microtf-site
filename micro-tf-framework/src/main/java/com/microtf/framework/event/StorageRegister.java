package com.microtf.framework.event;

import com.microtf.framework.services.BeanService;
import com.microtf.framework.services.SettingService;
import com.microtf.framework.services.storage.Config;
import com.microtf.framework.services.storage.StorageService;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.Properties;
import java.util.ServiceLoader;

/**
 * 存储自动注册
 *
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
        for (Map.Entry<String, Config> item : settingByClass.entrySet()) {

            Properties properties = new Properties();
            properties.put("config", item.getValue());
            properties.put("pathStart", item.getValue().getRootPath());
            Class<? extends StorageService> aClass = getClass(item.getValue().getCanonicalName());
            if (aClass != null) {
                log.info("注册{},类{}", item.getKey(), aClass.getCanonicalName());
                beanService.register(item.getKey(), aClass, properties);
            } else {
                log.info("注册{}失败原因找不到方法", item.getKey());
            }

        }
        log.info("注册存储事件 finish");
    }

    private Class<? extends StorageService> getClass(String canonicalName) {
        ServiceLoader<StorageService> load = ServiceLoader.load(StorageService.class);
        for (StorageService item : load) {
            if (item.getClass().getCanonicalName().equalsIgnoreCase(canonicalName)) {
                return item.getClass();
            }
        }
        return null;
    }
}
