package com.demo.spring;

import java.lang.reflect.Constructor;

/**
 * @Description: TODO
 * @Author: iWitness
 * @Date: 2025/6/26 11:40
 * @Version 1.0
 */
public class BeanDefinition {

    private Constructor<?> constructor;

    private String name;

    public BeanDefinition(Class<?> type) {
        Component component = type.getDeclaredAnnotation(Component.class);
        this.name = component.name().isEmpty() ? type.getSimpleName() : component.name();
        try {
            this.constructor = type.getConstructor();
        } catch (NoSuchMethodException e) {
            throw new RuntimeException(e);
        }
    }

    public String getBeanName() {
        return name;
    }

    public Constructor<?> getConstructor() {
        return constructor;
    }

}
