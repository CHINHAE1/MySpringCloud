package com.woniuxy;

import com.woniuxy.entity.utils.ResponseMyEntity;
import com.woniuxy.feign.ProductAPI;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.HashMap;

/**
 * OpenFeign API测试类
 */
@SpringBootTest
public class TestOpenFeignAPI {
    @Autowired
    ProductAPI productAPI;
    
    @Test
    public void testFindByPriceRange() {
        HashMap<String, Double> params = new HashMap<>();
        params.put("startPrice", 100.0);
        params.put("endPrice", 2000.0);
        ResponseMyEntity result = productAPI.findAll2(params);
        System.out.println(result);
    }
    
    @Test
    public void testFindByProductProperties() {
        com.woniuxy.entity.Product product = new com.woniuxy.entity.Product();
        product.setName("%1%");
        product.setPrice(1000.0);
        product.setStock(1000);
        ResponseMyEntity result = productAPI.findProduct(product);
        System.out.println(result);
    }
} 