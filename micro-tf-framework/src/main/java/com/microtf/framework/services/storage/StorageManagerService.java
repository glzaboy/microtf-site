package com.microtf.framework.services.storage;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.beans.factory.ListableBeanFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class StorageManagerService implements BeanFactoryAware {
    private BeanFactory beanFactory;
    @Override
    public void setBeanFactory(BeanFactory beanFactory) throws BeansException {
        this.beanFactory=beanFactory;
    }

    private List<StorageService> storageServiceList;

    @Autowired(required = false)
    public void setStorageServiceList(List<StorageService> storageServiceList) {
        this.storageServiceList = storageServiceList;
    }

    /**
     * 选择存储
     * @param objName
     * @return
     */
    public Optional<StorageService> selectStorage(String objName){
        ListableBeanFactory beanFactory = (ListableBeanFactory) this.beanFactory;
        Map<String, StorageService> beansOfType = beanFactory.getBeansOfType(StorageService.class);
        return beansOfType.values().stream().filter(item -> objName.startsWith(item.getPathStart())).findFirst();
    }
}
