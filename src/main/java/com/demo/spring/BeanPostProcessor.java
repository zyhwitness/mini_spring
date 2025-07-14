package com.demo.spring;

/**
 * @Description: TODO
 * @Author: iWitness
 * @Date: 2025/7/14 17:58
 * @Version 1.0
 */
public interface BeanPostProcessor {

    // 在bean初始化前调用
    default Object beforeInitialization(Object bean, String beanName){
        return bean;
    }


    // 在bean初始化后调用
    default Object afterInitialization(Object bean, String beanName){
        return bean;
    }

}
