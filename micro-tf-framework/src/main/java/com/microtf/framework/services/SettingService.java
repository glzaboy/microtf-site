package com.microtf.framework.services;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.microtf.framework.dto.SettingDto;
import com.microtf.framework.exceptions.BizException;
import com.microtf.framework.jpa.SettingRepository;
import com.microtf.framework.jpa.entity.SettingEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Example;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.*;

/**
 * 系统设置类
 *
 * @author glzaboy
 */
@Service
public class SettingService {
    private SettingRepository settingRepository;

    @Autowired
    public void setSettingRepository(SettingRepository settingRepository) {
        this.settingRepository = settingRepository;
    }

    private ObjectMapper objectMapper;

    @Autowired
    public void setObjectMapper(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    public <T extends SettingDto> Map<String,T> getSettingByClass(Class<T> classic) {
        SettingEntity settingEntity = new SettingEntity();
//        settingEntity.setName(name);
        settingEntity.setClassName(classic.getCanonicalName());
        List<SettingEntity> all = settingRepository.findAll(Example.of(settingEntity));
        Map<String,T> settings=new HashMap<>(16);
        for (SettingEntity item:all) {
            if (!item.getClassName().equals(classic.getCanonicalName())) {
                continue;
            }
            try {
                settings.put(item.getName(),objectMapper.readValue(item.getValue(),classic));
            } catch (JsonProcessingException e) {
                e.printStackTrace();
            }
        }
        return settings;
    }
    @Cacheable(value = "setting.bean", key = "#name")
    public <T extends SettingDto> T getSetting(String name, Class<T> classic) {
        SettingEntity settingEntity = new SettingEntity();
        settingEntity.setName(name);
        settingEntity.setClassName(classic.getCanonicalName());
        List<SettingEntity> all = settingRepository.findAll(Example.of(settingEntity));
        if(all.size() > 1){
            throw new BizException("配置文件不止一个无法进行处理");
        }
        Optional<SettingEntity> settingEntityOptional = all.stream().findFirst();
        SettingEntity settingEntity1 = settingEntityOptional.orElseGet(SettingEntity::new);
        if (classic.getCanonicalName().equals(settingEntity1.getClassName())) {
            try {
                T t = objectMapper.readValue(settingEntity1.getValue(), classic);
                t.setRead(true);
                return t;
            } catch (IOException e) {
                throw new BizException(e.getMessage());
            }
        } else {
            try {
                return classic.getDeclaredConstructor().newInstance();
            } catch (InvocationTargetException | NoSuchMethodException | InstantiationException | IllegalAccessException e) {
                throw new BizException(e.getMessage());
            }
        }
    }

    /**
     * 保存对象到设置数据库
     *
     * @param name 对象名称
     * @param bean 需要保存的对象要求此对象继承自 SettingData或其子类
     * @param <T>  类型限定为 SettingDto 的子类
     * @return 返回设置的对象值
     */
    @Transactional(rollbackOn = {BizException.class})
    @CacheEvict(value = {"setting.bean"}, key = "#name")
    public <T extends SettingDto> T saveSetting(String name, T bean) {
        SettingEntity settingEntity = new SettingEntity();
        settingEntity.setName(name);
        settingEntity.setClassName(bean.getClass().getCanonicalName());
        List<SettingEntity> all = settingRepository.findAll(Example.of(settingEntity));
        Optional<SettingEntity> first = all.stream().findFirst();
        SettingEntity settingEntity1 = first.orElseGet(SettingEntity::new);
        settingEntity1.setClassName(bean.getClass().getCanonicalName());
        settingEntity1.setName(name);
        try {
            settingEntity1.setValue(objectMapper.writeValueAsString(bean));
            settingRepository.save(settingEntity1);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            throw new BizException("数据序列化出错");
        }

        return bean;
    }
}
