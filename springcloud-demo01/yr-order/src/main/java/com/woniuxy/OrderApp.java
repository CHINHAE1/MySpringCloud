package com.woniuxy;

import com.netflix.loadbalancer.IRule;
import com.netflix.loadbalancer.RandomRule;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Bean;
import org.springframework.web.client.RestTemplate;

@SpringBootApplication
@EnableDiscoveryClient //开启服务发现
@EnableFeignClients//开启openfeign
public class OrderApp {
    //注册restTemplate成为bean才能再springboot项目中使用restTemplate
    @Bean
    @LoadBalanced //开启负载均衡，这个配置，才是决定了下面的请求能否轮训的配置
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }
    //配置 服务调用策略
    @Bean
    public IRule iRule() {
        //随机策略
        return new RandomRule();
    }


    public static void main(String[] args) {
        SpringApplication.run(OrderApp.class, args);
    }
}
