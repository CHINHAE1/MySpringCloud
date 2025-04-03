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
    
    // 使用CopyOnWriteArrayList保证线程安全，存储服务URL
    public static final CopyOnWriteArrayList<String> PRODUCT_SERVICE_URLS = new CopyOnWriteArrayList<>();
    
    // 使用AtomicInteger实现线程安全的计数器，用于轮询算法
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
        int size = PRODUCT_SERVICE_URLS.size(); // 获取服务列表大小
        if (size == 0) {
            throw new RuntimeException("没有可用的product service!");  // 没有可用服务时抛出异常
        }
        // 全局变量+1并取模，确定下标
        int index = COUNTER.getAndIncrement() % size; // 轮询算法核心，取模运算确保索引在列表范围内
        return PRODUCT_SERVICE_URLS.get(index); // 返回对应下标的URL
    }
    
    /**
     * 添加新的服务地址
     * @param url 新服务地址
     */
    public static void addProductServiceUrl(String url) {
        if (!PRODUCT_SERVICE_URLS.contains(url)) { // 检查是否已存在该服务
            PRODUCT_SERVICE_URLS.add(url);  // 添加新服务地址
            System.out.println("Added new product service: " + url); // 打印日志
        }
    }
    
    /**
     * 移除无效的服务地址
     * @param url 无效服务地址
     */
    public static void removeProductServiceUrl(String url) {
        PRODUCT_SERVICE_URLS.remove(url); // 从列表中移除服务
        System.out.println("Removed unavailable product service: " + url);
    }
    
    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate(); // 创建RestTemplate实例用于HTTP请求
    }
    
    /**
     * 获取当前所有可用服务列表
     * @return 服务列表
     */
    public static List<String> getAllProductServices() {
        return new ArrayList<>(PRODUCT_SERVICE_URLS); // 返回当前服务列表的副本
    }
} 