package com.demo.spring;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.net.URL;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @Description: TODO
 * @Author: iWitness
 * @Date: 2025/6/25 19:35
 * @Version 1.0
 */
public class ApplicationContext {

    public ApplicationContext(String packageName) throws Exception {
        initContext(packageName);
    }

    private final Map<String, BeanDefinition> beanDefinitionMap = new HashMap<>();

    // bean的容器, key是beanName, value是bean对象
    private final Map<String, Object> ioc = new HashMap<>();

    private final Map<String, Object> loadingIoc = new HashMap<>();

    private final List<BeanPostProcessor> beanPostProcessors = new ArrayList<>();

    public void initContext(String packageName) throws Exception {
        scanPackage(packageName).stream().filter(this::canCreate).forEach(this::wrapper);
        initBeanPostProcessor();
        // 把所有的BeanDefinition加载后，再创建bean
        beanDefinitionMap.values().forEach(this::createBean);
//        scanPackage(packageName).stream().filter(this::canCreate).map(this::wrapper).forEach(this::createBean);
//        List<BeanDefinition> list =
//                scanPackage(packageName).stream().filter(this::canCreate).map(this::wrapper).toList();

//        ApplicationContext.class.getClassLoader().getResource("");
//        List<Class<?>> componentClassList = scanPackage(packageName).
//                stream().
//                filter(aClass -> aClass.isAnnotationPresent(Component.class)).toList();
    }

    private void initBeanPostProcessor() {
        beanDefinitionMap.values().stream()
                .filter(beanDefinition -> BeanPostProcessor.class.isAssignableFrom(beanDefinition.getBeanType()))
                .map(this::createBean)
                .map(bean -> (BeanPostProcessor)bean)
                .forEach(beanPostProcessors::add);
    }

    // 判断什么样的类可以创建bean
    protected boolean canCreate(Class<?> type) {
        return type.isAnnotationPresent(Component.class);
    }

    // 转换成BeanDefinition
    protected BeanDefinition wrapper(Class<?> type) {
        BeanDefinition beanDefinition = new BeanDefinition(type);
        if (beanDefinitionMap.containsKey(beanDefinition.getBeanName())) {
            throw new RuntimeException("beanName重复");
        }
        beanDefinitionMap.put(beanDefinition.getBeanName(), beanDefinition);
        return beanDefinition;
    }

    // 通过BeanDefinition创建bean
    protected Object createBean(BeanDefinition beanDefinition) {
        String beanName = beanDefinition.getBeanName();
        if (ioc.containsKey(beanName)) {
            return ioc.get(beanName);
        }
        if (loadingIoc.containsKey(beanName)) {
            return loadingIoc.get(beanName);
        }
        return doCreateBean(beanDefinition);
    }

    private Object doCreateBean(BeanDefinition beanDefinition) {
        Constructor<?> constructor = beanDefinition.getConstructor();
        Object bean = null;
        try {
            bean = constructor.newInstance();
            loadingIoc.put(beanDefinition.getBeanName(), bean);
            autowiredBean(bean, beanDefinition);
            bean = initializeBean(bean, beanDefinition);
            loadingIoc.remove(beanDefinition.getBeanName());
            ioc.put(beanDefinition.getBeanName(), bean);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        return bean;
    }

    private Object initializeBean(Object bean, BeanDefinition beanDefinition) throws Exception {
        for (BeanPostProcessor beanPostProcessor : beanPostProcessors) {
            bean = beanPostProcessor.beforeInitialization(bean, beanDefinition.getBeanName());
        }

        Method postConstructMethod = beanDefinition.getPostConstructMethod();
        if (postConstructMethod != null) {
            postConstructMethod.invoke(bean);
        }

        for (BeanPostProcessor beanPostProcessor : beanPostProcessors) {
            bean = beanPostProcessor.afterInitialization(bean, beanDefinition.getBeanName());
        }

        return bean;
    }

    private void autowiredBean(Object bean, BeanDefinition beanDefinition) throws IllegalAccessException {
        for (Field autowiredField : beanDefinition.getAutowiredFields()) {
            autowiredField.setAccessible(true);
            autowiredField.set(bean, getBean(autowiredField.getType()));
        }
    }

    // 扫描指定包下的所有类，不一定是一种类型的类
    private List<Class<?>> scanPackage(String packageName) throws Exception {
        List<Class<?>> classList = new ArrayList<>();
        // a.b.c
        URL resource = this.getClass().getClassLoader().getResource(packageName.replace(".", File.separator));
        Path path = Path.of(resource.getFile());
        // 递归遍历path目录下的所有文件
        Files.walkFileTree(path, new SimpleFileVisitor<>() {
            @Override
            public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                Path absolutePath = file.toAbsolutePath();
                // 判断是否是class文件
                if (absolutePath.toString().endsWith(".class")) {
                    // 既能拿到文件夹下的文件，也能拿到子文件夹下的文件
                    System.out.println(absolutePath);

                    String replaceStr = absolutePath.toString().replace(File.separator, ".");
                    // packageName首次出现的位置
                    int packageIndex = replaceStr.indexOf(packageName);
                    // 拿到全类名
                    String className = replaceStr.substring(packageIndex, replaceStr.length() - ".class".length());
                    System.out.println(className);
                    try {
                        // 拿到类对象
                        classList.add(Class.forName(className));
                    } catch (ClassNotFoundException e) {
                        throw new RuntimeException(e);
                    }
                }
                // 无论是否是class文件，都继续遍历
                return FileVisitResult.CONTINUE;
            }
        });

        // 拿到所有的类对象
        return classList;
    }

    // 根据beanName获取bean
    public Object getBean(String beanName) {
        if (beanName == null) {
            return null;
        }
        Object bean = this.ioc.get(beanName);
        if (bean != null) {
            return bean;
        }
        if (beanDefinitionMap.containsKey(beanName)) {
            return createBean(beanDefinitionMap.get(beanName));
        }
        return null;
    }

    // 根据beanType获取bean
    public <T> T getBean(Class<T> beanType) {
        String beanName = this.beanDefinitionMap.values().stream()
                .filter(beanDefinition -> beanType.isAssignableFrom(beanDefinition.getBeanType()))
                .map(BeanDefinition::getBeanName)
                .findFirst()
                .orElse(null);

        return (T) getBean(beanName);

//        return this.ioc.values().stream()
//                .filter(bean -> beanType.isAssignableFrom(bean.getClass()))
//                .map(bean -> (T) bean)
//                .findAny()
//                .orElse(null);
    }

    // 获取同一种类型的bean集合
    public <T> List<T> getBeans(Class<T> beanType) {
        return this.beanDefinitionMap.values().stream()
                .filter(beanDefinition -> beanType.isAssignableFrom(beanDefinition.getBeanType()))
                .map(BeanDefinition::getBeanName)
                .map(this::getBean)
                .map(bean -> (T) bean)
                .toList();

//        return this.ioc.values().stream()
//                .filter(bean -> beanType.isAssignableFrom(bean.getClass()))
//                .map(bean -> (T) bean)
//                .toList();
    }

}
