package com.woniuxy;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Bean;
import org.springframework.web.client.RestTemplate;

/**
 * @Author: 马宇航
 * @Todo: TODO
 * @DateTime: 25/04/01/星期二 17:31
 * @Component: 成都蜗牛学苑
 **/
@SpringBootApplication
@EnableDiscoveryClient
@EnableFeignClients
public class ProductAPP {
    public static void main(String[] args) {
        SpringApplication.run(ProductAPP.class, args);
    }
    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }
}