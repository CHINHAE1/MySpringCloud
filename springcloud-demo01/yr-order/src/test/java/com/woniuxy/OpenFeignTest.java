package com.woniuxy;

import com.woniuxy.entity.Product;
import com.woniuxy.entity.utils.ResponseMyEntity;
import com.woniuxy.feign.ProductAPI;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.math.BigDecimal;


@SpringBootTest
public class OpenFeignTest {

    @Autowired  // 去掉 static
    private ProductAPI productAPI;

    @Test  // 使用 @Test 注解
    public void testFindAll() {
        Product product = new Product();
        product.setName("%1%");
        product.setPrice(101.0);
        product.setStock(1000);
        ResponseMyEntity all = productAPI.findProduct(product);
        System.out.println(all);
    }
}
