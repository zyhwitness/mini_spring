package com.demo.spring.sub;

import com.demo.spring.BeanPostProcessor;
import com.demo.spring.Component;

/**
 * @Description: TODO
 * @Author: iWitness
 * @Date: 2025/7/14 19:53
 * @Version 1.0
 */
@Component
public class MyBeanPostProcessor implements BeanPostProcessor {

    @Override
    public Object afterInitialization(Object bean, String beanName) {
        System.out.println(beanName + " init finished");
        return bean;
    }
}
