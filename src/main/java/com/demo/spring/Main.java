package com.demo.spring;

/**
 * @Description: TODO
 * @Author: iWitness
 * @Date: 2025/6/30 11:29
 * @Version 1.0
 */
public class Main {
    public static void main(String[] args) throws Exception {
        ApplicationContext ioc = new ApplicationContext("com.demo.spring");
        Object cat = ioc.getBean("cat");
        System.out.println(cat);
    }
}
