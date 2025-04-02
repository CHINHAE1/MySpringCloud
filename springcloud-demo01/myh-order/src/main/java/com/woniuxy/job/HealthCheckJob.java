package com.woniuxy.job;

import com.woniuxy.config.RestTemplateConfig;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;

/**
 * 定时健康检查任务
 * 定期检查所有产品服务的健康状态
 */
@Component
@EnableScheduling
public class HealthCheckJob {
    
    private final RestTemplate restTemplate;
    
    public HealthCheckJob(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }
    
    /**
     * 每60秒执行一次健康检查
     */
    @Scheduled(fixedRate = 60000)
    public void checkServiceHealth() {
        System.out.println("开始执行服务健康检查...");
        
        List<String> unhealthyServices = new ArrayList<>();
        
        // 检查所有产品服务
        for (String url : RestTemplateConfig.getAllProductServices()) {
            try {
                restTemplate.getForEntity(url + "/product/list", Object.class);
                System.out.println("服务健康检查: " + url + " - 正常");
            } catch (RestClientException e) {
                System.out.println("服务健康检查: " + url + " - 异常");
                // 记录不健康的服务
                unhealthyServices.add(url);
            }
        }
        
        // 从列表中移除不健康的服务
        for (String url : unhealthyServices) {
            RestTemplateConfig.removeProductServiceUrl(url);
        }
        
        System.out.println("健康检查完成, 当前可用服务数量: " + RestTemplateConfig.PRODUCT_SERVICE_URLS.size());
    }
} 