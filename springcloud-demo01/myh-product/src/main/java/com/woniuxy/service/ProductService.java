package com.woniuxy.service;

import com.woniuxy.entity.Product;

import java.util.List;

/**
 * 产品服务接口
 */
public interface ProductService {
    
    /**
     * 查询所有产品
     * @return 产品列表
     */
    List<Product> findAllProducts();
    
    /**
     * 根据ID查询产品
     * @param pid 产品ID
     * @return 产品对象
     */
    Product findProductById(Integer pid);
    
    /**
     * 保存或更新产品
     * @param product 产品对象
     * @return 保存后的产品对象
     */
    Product saveProduct(Product product);
    
    /**
     * 扣减商品库存
     * @param pid 商品ID
     * @param num 扣减数量
     * @return 更新后的商品对象
     */
    Product deductProductStock(Integer pid, Integer num);
} 