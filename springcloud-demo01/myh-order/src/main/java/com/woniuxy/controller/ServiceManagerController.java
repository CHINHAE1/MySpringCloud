package com.woniuxy.controller;

import com.woniuxy.config.RestTemplateConfig;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 服务管理控制器
 * 用于动态添加和删除服务
 */
@RestController
@RequestMapping("/service")
public class ServiceManagerController {

    private final RestTemplate restTemplate; // 用于发送HTTP请求

    public ServiceManagerController(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    /**
     * 获取所有当前可用的产品服务
     * 
     * @return 服务列表
     */
    @GetMapping("/list")
    public List<String> getAllServices() {
        return RestTemplateConfig.getAllProductServices();
    }

    /**
     * 添加新的产品服务
     * 
     * @param host 主机地址
     * @param port 端口
     * @return 操作结果
     */
    @PostMapping("/add")
    public Map<String, Object> addService(@RequestParam String host, @RequestParam int port) {
        Map<String, Object> result = new HashMap<>();
        String serviceUrl = "http://" + host + ":" + port;

        // 测试服务是否可用
        try {
            restTemplate.getForEntity(serviceUrl + "/product/list", Object.class); // 发送请求检查服务可用性
            RestTemplateConfig.addProductServiceUrl(serviceUrl); // 添加服务
            result.put("success", true); // 设置成功标志
            result.put("message", "Service added successfully: " + serviceUrl); // 设置成功消息
        } catch (RestClientException e) {
            result.put("success", false); // 设置失败标志
            result.put("message", "Service unavailable: " + serviceUrl); // 设置失败消息
        }

        return result;
    }

    /**
     * 移除产品服务
     * 
     * @param host 主机地址
     * @param port 端口
     * @return 操作结果
     */
    @DeleteMapping("/remove")
    public Map<String, Object> removeService(@RequestParam String host, @RequestParam int port) { // 接收主机和端口参数
        Map<String, Object> result = new HashMap<>();
        String serviceUrl = "http://" + host + ":" + port; // 构建服务URL

        if (RestTemplateConfig.PRODUCT_SERVICE_URLS.contains(serviceUrl)) { // 检查服务是否存在
            RestTemplateConfig.removeProductServiceUrl(serviceUrl); // 移除服务
            result.put("success", true); // 设置成功标志
            result.put("message", "Service removed successfully: " + serviceUrl); // 设置成功消息
        } else {
            result.put("success", false); // 设置失败标志
            result.put("message", "Service not found: " + serviceUrl); // 设置失败消息
        }

        return result;
    }

    /**
     * 检查所有服务的健康状态
     * 
     * @return 健康检查结果
     */
    @GetMapping("/health")
    public Map<String, Boolean> checkServicesHealth() {
        Map<String, Boolean> healthStatus = new HashMap<>();

        for (String url : RestTemplateConfig.getAllProductServices()) {
            try {
                ResponseEntity<Object> response = restTemplate.getForEntity(url + "/product/list", Object.class); // 发送请求检查服务
                healthStatus.put(url, response.getStatusCode().is2xxSuccessful()); // 根据响应状态码判断服务健康状态
            } catch (Exception e) {
                healthStatus.put(url, false); // 服务异常
                // 自动移除不健康的服务
                RestTemplateConfig.removeProductServiceUrl(url);
            }
        }

        return healthStatus; // 返回健康状态Map
    }

    /**
     * 注册服务
     * @param host 主机地址
     * @param port 端口
     * @return 操作结果
     */
    @PostMapping("/register")
    public Map<String, Object> registerService(@RequestParam String host, @RequestParam String port) {
        Map<String, Object> result = new HashMap<>();
        String serviceUrl = "http://" + host + ":" + port;

        System.out.println("收到服务注册请求: " + serviceUrl);

        // 测试服务是否可用
        try {
            restTemplate.getForEntity(serviceUrl + "/product/list", Object.class);
            RestTemplateConfig.addProductServiceUrl(serviceUrl);
            result.put("success", true);
            result.put("message", "Service registered successfully: " + serviceUrl);
        } catch (RestClientException e) {
            result.put("success", false);
            result.put("message", "Service unavailable: " + serviceUrl);
        }

        return result;
    }


}