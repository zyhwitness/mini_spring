package com.demo.spring.sub;

import com.demo.spring.Autowired;
import com.demo.spring.Component;
import com.demo.spring.PostConstruct;

/**
 * @Description: TODO
 * @Author: iWitness
 * @Date: 2025/7/1 19:58
 * @Version 1.0
 */
@Component(name = "myDog")
public class Dog {

    @Autowired
    private Cat cat;

    @Autowired
    private Dog dog;

    @PostConstruct
    public void init() {
        System.out.println("Dog init" + cat);
        System.out.println("Dog init" + dog);
    }

}
