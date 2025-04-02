package com.woniuxy;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * @Author: chinhae
 * @Todo: TODO
 * @DateTime: 25/04/01/星期二 17:31
 * @Component: 成都蜗牛学苑
 **/
@SpringBootApplication
@EnableScheduling
public class OrderAPP {
    public static void main(String[] args) {
        SpringApplication.run(OrderAPP.class, args);
    }
}