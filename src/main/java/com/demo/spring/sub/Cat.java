package com.demo.spring.sub;

import com.demo.spring.Autowired;
import com.demo.spring.Component;
import com.demo.spring.PostConstruct;

/**
 * @Description: TODO
 * @Author: iWitness
 * @Date: 2025/6/30 11:38
 * @Version 1.0
 */
@Component
public class Cat {

    @Autowired
    private Dog dog;

    @PostConstruct
    public void init() {
        System.out.println("Cat init" + dog);
    }

}
