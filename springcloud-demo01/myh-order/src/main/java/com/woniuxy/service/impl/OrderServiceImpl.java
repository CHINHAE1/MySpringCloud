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
        String baseUrl = getProductServiceUrlWithFallback();
        String url = baseUrl + "/product/list";
        
        try {
            ResponseEntity<List<Product>> response = restTemplate.exchange(
                    url,
                    HttpMethod.GET,
                    null,
                    new ParameterizedTypeReference<List<Product>>() {}
            );
            return response.getBody();
        } catch (RestClientException e) {
            // 服务不可用，移除当前服务地址
            RestTemplateConfig.removeProductServiceUrl(baseUrl);
            // 重试另一个服务
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
            product = restTemplate.getForEntity(productUrl, Product.class).getBody();
        } catch (RestClientException e) {
            // 服务不可用，移除当前服务地址
            RestTemplateConfig.removeProductServiceUrl(baseUrl);
            return createOrder(uid, pid, number); // 重试
        }
        
        if (product == null) {
            throw new Exception("商品不存在");
        }
        
        // 2. 调用product的接口扣减库存
        String deductUrl = getProductServiceUrlWithFallback() + "/product/deduct?pid=" + pid + "&num=" + number;
        try {
            restTemplate.postForEntity(deductUrl, null, Product.class);
        } catch (RestClientException e) {
            // 服务不可用，移除当前服务地址并重试
            RestTemplateConfig.removeProductServiceUrl(baseUrl);
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
        
        return "success - used service: " + baseUrl;
    }
    
    /**
     * 获取产品服务URL，如果无可用服务则抛出异常
     * @return 服务URL
     */
    private String getProductServiceUrlWithFallback() {
        if (RestTemplateConfig.PRODUCT_SERVICE_URLS.isEmpty()) {
            // 如果没有可用服务，尝试恢复默认服务列表
            RestTemplateConfig.addProductServiceUrl("http://localhost:8081");
            RestTemplateConfig.addProductServiceUrl("http://localhost:8082");
            RestTemplateConfig.addProductServiceUrl("http://localhost:8083");
            
            if (RestTemplateConfig.PRODUCT_SERVICE_URLS.isEmpty()) {
                throw new RuntimeException("No available product services!");
            }
        }
        return RestTemplateConfig.getNextProductServiceUrl();
    }
}