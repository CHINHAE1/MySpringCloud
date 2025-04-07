package com.woniuxy.service;

import com.woniuxy.entity.Product;
import java.util.List;

public interface ProductService {

    Product findById(Integer pid);
    
    void save(Product product);
    
    int update(Integer pid, Integer num);
    
    List<Product> findByPrice(double startPrice, double endPrice);
    
    List<Product> findOne(String name, double price, Integer stock);

    void minusById(Integer pid, Integer count);
}
