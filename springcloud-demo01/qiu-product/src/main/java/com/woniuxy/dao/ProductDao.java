package com.woniuxy.dao;

import com.woniuxy.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ProductDao extends JpaRepository<Product,Integer> {
    List<Product> findAll();
    Product save(Product product);
    Product findProductByPid(Integer pid);
//    void deleteProductByPid(Integer pid);
    List<Product> findByNameLikeOrPriceOrStock(String name,Double price,Integer stock);
}
