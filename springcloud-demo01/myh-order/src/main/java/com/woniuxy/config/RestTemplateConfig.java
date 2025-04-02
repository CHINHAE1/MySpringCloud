package com.woniuxy.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * RestTemplate配置类
 */
@Configuration
public class RestTemplateConfig {
    
    // 使用线程安全的列表存储服务地址
    public static final CopyOnWriteArrayList<String> PRODUCT_SERVICE_URLS = new CopyOnWriteArrayList<>();
    
    // 计数器，用于轮询
    private static final AtomicInteger COUNTER = new AtomicInteger(0);
    
    static {
        // 初始化产品服务地址列表
        PRODUCT_SERVICE_URLS.add("http://localhost:8081");
        PRODUCT_SERVICE_URLS.add("http://localhost:8082");
        PRODUCT_SERVICE_URLS.add("http://localhost:8083");
    }
    
    /**
     * 获取下一个服务地址（轮询方式）
     * @return 服务地址
     */
    public static String getNextProductServiceUrl() {
        int size = PRODUCT_SERVICE_URLS.size();
        if (size == 0) {
            throw new RuntimeException("No available product service!");
        }
        int index = COUNTER.getAndIncrement() % size;
        return PRODUCT_SERVICE_URLS.get(index);
    }
    
    /**
     * 添加新的服务地址
     * @param url 新服务地址
     */
    public static void addProductServiceUrl(String url) {
        if (!PRODUCT_SERVICE_URLS.contains(url)) {
            PRODUCT_SERVICE_URLS.add(url);
            System.out.println("Added new product service: " + url);
        }
    }
    
    /**
     * 移除无效的服务地址
     * @param url 无效服务地址
     */
    public static void removeProductServiceUrl(String url) {
        PRODUCT_SERVICE_URLS.remove(url);
        System.out.println("Removed unavailable product service: " + url);
    }
    
    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }
    
    /**
     * 获取当前所有可用服务列表
     * @return 服务列表
     */
    public static List<String> getAllProductServices() {
        return new ArrayList<>(PRODUCT_SERVICE_URLS);
    }
} 