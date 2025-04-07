package com.woniuxy;

import com.netflix.loadbalancer.IRule;
import com.netflix.loadbalancer.RandomRule;
import feign.Retryer;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Bean;
import org.springframework.web.client.RestTemplate;

/**
 * @Author: 刘心雨
 * @Todo: TODO
 * @DateTime: 25/04/01/星期二 18:32
 * @Component: 成都蜗牛学苑
 **/
@SpringBootApplication
@EnableDiscoveryClient
@EnableFeignClients
public class OrderAPP {
    public static void main(String[] args) {
        SpringApplication.run(OrderAPP.class, args);
    }

    @Bean
    @LoadBalanced // 开启负载均衡，这个配置，才是决定了下面的请求能否轮训的配置
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }
    
    // 配置服务调用策略
    @Bean
    public IRule iRule() {
        // 随机策略
        return new RandomRule();
    }
    
    // 配置Feign重试
    @Bean
    public Retryer retryRule() {
        // 100毫秒后重试，最大重试时间为3000毫秒，重试次数为3次
        return new Retryer.Default(100L, 3000L, 3);
    }
}
