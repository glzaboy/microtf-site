package com.microtf.framework.services.storage;

import org.jetbrains.annotations.NotNull;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.beans.factory.ListableBeanFactory;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Optional;

/**
 * 存储管理
 *
 * @author glzaboy
 */
@Service
public class StorageManagerService implements BeanFactoryAware {
    private BeanFactory beanFactory;

    @Override
    public void setBeanFactory(@NotNull BeanFactory beanFactory) throws BeansException {
        this.beanFactory = beanFactory;
    }

    /**
     * 选择存储
     *
     * @param objName 存储名称
     * @return 存储引擎
     */
    public Optional<StorageService> selectStorage(String objName) {
        ListableBeanFactory beanFactory = (ListableBeanFactory) this.beanFactory;
        Map<String, StorageService> beansOfType = beanFactory.getBeansOfType(StorageService.class);
        return beansOfType.values().stream().filter(item -> objName.startsWith(item.getPathStart())).findFirst();
    }
}
