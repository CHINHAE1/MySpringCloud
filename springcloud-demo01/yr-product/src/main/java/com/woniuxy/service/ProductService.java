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
    
    /**
     * 根据库存范围查询商品
     * @param minStock 最小库存值（包含）
     * @param maxStock 最大库存值（包含）
     * @return 符合库存范围的商品列表
     */
    List<Product> findByStockBetween(Integer minStock, Integer maxStock);
    
    /**
     * 查询指定库存值的商品
     * @param stock 库存值
     * @return 符合条件的商品列表
     */
    List<Product> findByStock(Integer stock);
}
