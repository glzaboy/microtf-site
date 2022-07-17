package com.microtf.framework.services;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.beans.factory.support.AbstractBeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.stereotype.Service;

import java.util.Enumeration;
import java.util.Properties;

@Service
public class BeanService implements BeanFactoryAware {
    BeanFactory beanFactory;
    @Override
    public void setBeanFactory(BeanFactory beanFactory) throws BeansException {
        this.beanFactory=beanFactory;
    }
    private final String prefix="beanPrefix";
    public void register(Class<?> classic){
        register(classic,null);
    }
    public void register(Class<?> classic, Properties properties){
        char[] chars = (classic.getSimpleName()).toCharArray();
        chars[0]=Character.toLowerCase(chars[0]);
        register(new String(chars),classic,properties);
    }
    public void register(String beanName, Class<?> classic){
        register(beanName,classic,null);
    }
    public void register(String beanName, Class<?> classic, Properties properties){
        BeanDefinitionBuilder beanDefinitionBuilder=BeanDefinitionBuilder.genericBeanDefinition(classic);
        char[] chars = (prefix+beanName).toCharArray();
        chars[0]=Character.toLowerCase(chars[0]);
        BeanDefinitionRegistry beanFactory = (BeanDefinitionRegistry) this.beanFactory;
        if(properties!=null){
            @SuppressWarnings("unchecked")
            Enumeration<String> stringEnumeration = (Enumeration<String>) properties.propertyNames();
            while (stringEnumeration.hasMoreElements()){
                String o = stringEnumeration.nextElement();
                beanDefinitionBuilder.addPropertyValue(o,properties.get(o));
            }
        }
        AbstractBeanDefinition beanDefinition = beanDefinitionBuilder.getRawBeanDefinition();
        beanFactory.registerBeanDefinition(new String(chars),beanDefinition);
    }
    public <T> T getBean(Class<T> classic){
        return getBean(classic.getSimpleName(),classic);
    }
    public <T> T getBean(String beanName,Class<T> classic){
        char[] chars = (prefix+beanName).toCharArray();
        chars[0]=Character.toLowerCase(chars[0]);
        return beanFactory.getBean(new String(chars), classic);
    }
    public void unLoad(Class<?> classic){
        unLoad(classic.getSimpleName());
    }
    public void unLoad(String beanName){
        char[] chars = (prefix+beanName).toCharArray();
        chars[0]=Character.toLowerCase(chars[0]);
        BeanDefinitionRegistry beanFactory = (BeanDefinitionRegistry) this.beanFactory;
        beanFactory.removeBeanDefinition(new String(chars));
    }
}
