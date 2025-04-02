package com.woniuxy.service.impl;

import com.woniuxy.entity.Product;
import com.woniuxy.repository.ProductRepository;
import com.woniuxy.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 产品服务实现类
 */
@Service
public class ProductServiceImpl implements ProductService {
    
    @Autowired
    private ProductRepository productRepository;
    
    @Override
    public List<Product> findAllProducts() {
        return productRepository.findAll();
    }
    
    @Override
    public Product findProductById(Integer pid) {
        return productRepository.findByPid(pid);
    }
    
    @Override
    public Product saveProduct(Product product) {
        return productRepository.save(product);
    }
    
    @Override
    public Product deductProductStock(Integer pid, Integer num) {
        // 查询商品
        Product product = productRepository.findByPid(pid);
        if (product == null) {
            throw new RuntimeException("商品不存在");
        }
        
        // 判断库存是否足够
        if (product.getStock() < num) {
            throw new RuntimeException("库存不足");
        }
        
        // 扣减库存
        product.setStock(product.getStock() - num);
        
        // 保存更新
        return productRepository.save(product);
    }
} 