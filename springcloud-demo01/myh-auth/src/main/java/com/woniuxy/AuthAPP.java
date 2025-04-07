package com.woniuxy;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.context.annotation.ApplicationScope;

/**
 * @Author: 马宇航
 * @Todo: TODO
 * @DateTime: 25/04/01/星期二 17:31
 * @Component: 成都蜗牛学苑
 **/
@SpringBootApplication
public class AuthAPP {
    public static void main(String[] args) {
        SpringApplication.run(AuthAPP.class, args);
    }
    @Bean
    RestTemplate restTemplate() {
        return new RestTemplate();
    }
}
