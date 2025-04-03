package com.woniuxy.job;

import com.woniuxy.config.RestTemplateConfig;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 定时健康检查任务
 * 定期检查所有产品服务的健康状态
 */
@Component
@EnableScheduling // 启用定时任务
public class HealthCheckJob {
    
    private final RestTemplate restTemplate; // 用于发送HTTP请求检查服务健康状态

    // 存储服务最后一次健康时间
    private Map<String, Long> serviceLastHealthyTime = new ConcurrentHashMap<>();

    // 服务容忍不健康的最长时间（毫秒）
    private static final long SERVICE_TOLERANCE_TIME = 90000; // 90秒
    
    public HealthCheckJob(RestTemplate restTemplate) {
        this.restTemplate = restTemplate; 
    }
    
    /**
     * 每60秒执行一次健康检查
     */
    @Scheduled(fixedRate = 30000) // 设置定时任务，每30秒执行一次
    public void checkServiceHealth() {

        System.out.println("开始执行服务健康检查...");
        
        // 创建列表存储不健康的服务
        List<String> unhealthyServices = new ArrayList<>();  
        
        // 检查所有产品服务
        // 遍历所有服务
        for (String url : RestTemplateConfig.getAllProductServices()) { 
            try {
                // 发送请求检查服务是否可用
                restTemplate.getForEntity(url + "/product/list", Object.class); 
                System.out.println("服务健康检查: " + url + " - 正常");
            } catch (RestClientException e) {
                System.out.println("服务健康检查: " + url + " - 异常");

                // 检查是否超过容忍时间
                Long lastHealthy = serviceLastHealthyTime.get(url);
                if (lastHealthy == null || System.currentTimeMillis() - lastHealthy > SERVICE_TOLERANCE_TIME) {
                    // 超过容忍时间，添加到移除列表
                    unhealthyServices.add(url);
                    System.out.println("服务已超过容忍时间，将被移除: " + url);
                } else {
                    System.out.println("服务暂时不可用，但仍在容忍期内: " + url);
                }
            }
        }
        
        // 从列表中移除不健康的服务
        for (String url : unhealthyServices) {
            // 移除不健康服务
            RestTemplateConfig.removeProductServiceUrl(url); 
            serviceLastHealthyTime.remove(url);
        }
        
        System.out.println("健康检查完成, 当前可用服务数量: " + RestTemplateConfig.PRODUCT_SERVICE_URLS.size());
    }
} 