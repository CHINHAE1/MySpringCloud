package com.woniuxy.service.impl;

import com.woniuxy.config.RestTemplateConfig;
import com.woniuxy.entity.Order;
import com.woniuxy.entity.Product;
import com.woniuxy.repository.OrderRepository;
import com.woniuxy.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.util.List;

/**
 * 订单服务实现类
 */
@Service
public class OrderServiceImpl implements OrderService {
    
    @Autowired
    private RestTemplate restTemplate;
    
    @Autowired
    private OrderRepository orderRepository;
    
    @Override
    public List<Product> findAllProducts() {
        // 获取下一个服务地址
        String baseUrl = getProductServiceUrlWithFallback(); // 获取服务地址
        String url = baseUrl + "/product/list"; // 构建完整URL
        
        try {
            ResponseEntity<List<Product>> response = restTemplate.exchange(
                    url,
                    HttpMethod.GET,
                    null,
                    new ParameterizedTypeReference<List<Product>>() {} 
            );
            // 返回的Product对象中包含了端口信息
            return response.getBody();
        } catch (RestClientException e) {
            // 服务不可用，移除当前服务地址
            RestTemplateConfig.removeProductServiceUrl(baseUrl); 
            // 递归调用重试另一个服务
            return findAllProducts(); 
        }
    }
    
    @Override
    public String createOrder(Integer uid, Integer pid, Integer number) throws Exception {
        // 1. 先通过商品的pid查询商品信息
        String baseUrl = getProductServiceUrlWithFallback(); 
        String productUrl = baseUrl + "/product/" + pid; 
        
        Product product;
        try {
            // 发送GET请求获取商品
            product = restTemplate.getForEntity(productUrl, Product.class).getBody(); 
        } catch (RestClientException e) {
            // 服务不可用，移除当前服务地址
            RestTemplateConfig.removeProductServiceUrl(baseUrl); 
            // 递归重试
            return createOrder(uid, pid, number); 
        }
        
        if (product == null) {
            throw new Exception("商品不存在"); 
        }
        
        // 2. 调用product的接口扣减库存
        // 构建扣减库存URL
        String deductUrl = getProductServiceUrlWithFallback() + "/product/deduct?pid=" + pid + "&num=" + number; 
        try {
            // 发送POST请求扣减库存
            restTemplate.postForEntity(deductUrl, null, Product.class); 
        } catch (RestClientException e) {
            // 服务不可用，移除当前服务地址并重试
            RestTemplateConfig.removeProductServiceUrl(baseUrl);
            // 递归重试
            return createOrder(uid, pid, number); 
        }
        
        // 3. 创建订单对象
        Order order = new Order();
        order.setUid(uid);
        order.setPid(pid);
        order.setNumber(number);
        order.setPname(product.getName());
        order.setPrice(product.getPrice());
        
        // 4. 保存订单信息
        orderRepository.save(order);
        
        // 返回成功信息和使用的服务地址
        return "success - used service: " + baseUrl; 
    }
    
    /**
     * 获取产品服务URL，如果无可用服务则抛出异常
     * @return 服务URL
     */
    private String getProductServiceUrlWithFallback() {
        if (RestTemplateConfig.PRODUCT_SERVICE_URLS.isEmpty()) {
            System.out.println("警告：没有可用的产品服务，尝试恢复默认服务列表");
            
            // 尝试连接默认端口的服务
            String[] defaultPorts = {"8081", "8082", "8083"};
            for (String port : defaultPorts) {
                try {
                    String url = "http://localhost:" + port;
                    restTemplate.getForEntity(url + "/product/list", Object.class);
                    RestTemplateConfig.addProductServiceUrl(url);
                    System.out.println("成功恢复服务: " + url);
                } catch (Exception e) {
                    System.out.println("尝试恢复服务失败: http://localhost:" + port);
                }
            }
            
            if (RestTemplateConfig.PRODUCT_SERVICE_URLS.isEmpty()) {
                throw new RuntimeException("没有可用的产品服务!");
            }
        }
        return RestTemplateConfig.getNextProductServiceUrl();
    }
    
}