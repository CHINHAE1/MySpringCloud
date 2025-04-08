package com.woniuxy.service;

import com.woniuxy.entity.Product;

import java.util.List;
import java.util.Optional;

public interface ProductService {
    List<Product> findAll();

    Product save(Product product);

    Product findProductByPid(Integer id);
    void deleteProductByPid(Integer pid);
    List<Product> findOne(String name,Double price,Integer stock);
}
