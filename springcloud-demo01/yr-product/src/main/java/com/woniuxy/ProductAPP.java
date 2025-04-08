package com.woniuxy;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
@EnableDiscoveryClient //开启服务发现
public class ProductAPP {
    public static void main(String[] args) {
        SpringApplication.run(ProductAPP.class, args);
    }
}
