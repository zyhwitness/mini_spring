package com.demo.spring;

import com.demo.spring.sub.Autowired;
import com.demo.spring.sub.PostConstruct;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;

/**
 * @Description: TODO
 * @Author: iWitness
 * @Date: 2025/6/26 11:40
 * @Version 1.0
 */
public class BeanDefinition {

    private final Constructor<?> constructor;

    private final String name;

    private final Method postConstructMethod;

    private final List<Field> autowiredFields;

    private final Class<?> beanType;

    public BeanDefinition(Class<?> type) {
        this.beanType = type;
        Component component = type.getDeclaredAnnotation(Component.class);
        this.name = component.name().isEmpty() ? type.getSimpleName() : component.name();
        try {
            this.constructor = type.getConstructor();

            // 获取所有被@PostConstruct注解的方法
            this.postConstructMethod = Arrays.stream(type.getDeclaredMethods())
                    .filter(m -> m.isAnnotationPresent(PostConstruct.class))
                    .findFirst().orElse(null);

            // 获取所有被@Autowired注解的成员变量
            this.autowiredFields = Arrays.stream(type.getDeclaredFields())
                    .filter(f -> f.isAnnotationPresent(Autowired.class)).toList();

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

    public Method getPostConstructMethod() {
        return postConstructMethod;
    }

    public List<Field> getAutowiredFields() {
        return autowiredFields;
    }

    public Class<?> getBeanType() {
        return beanType;
    }
}
