package com.demo.spring;

import java.util.List;

/**
 * @Description: TODO
 * @Author: iWitness
 * @Date: 2025/6/25 19:35
 * @Version 1.0
 */
public class ApplicationContext {

    public ApplicationContext(String packageName) {
        initContext(packageName);
    }

    public void initContext(String packageName) {
        // ApplicationContext.class.getClassLoader().getResource("");
        List<Class<?>> componentClassList = scanPackage(packageName).
                stream().
                filter(aClass -> aClass.isAnnotationPresent(Component.class)).toList();
    }

    protected boolean scanCreate(Class<?> type){
        return type.isAnnotationPresent(Component.class);
    }


    // 扫描指定包下的所有类，不一定是一种类型的类
    private List<Class<?>> scanPackage(String packageName) {
        return null;
    }

    public Object getBean(String beanName) {
        return null;
    }

    public <T> T getBean(Class<T> beanType) {
        return null;
    }

    // 获取同一种类型的bean集合
    public <T> List<T> getBeans(Class<T> beanType) {
        return null;
    }

}
