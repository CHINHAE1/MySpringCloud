package com.woniuxy.repository;

import com.woniuxy.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * 产品数据访问层
 */
@Repository
public interface ProductRepository extends JpaRepository<Product, Integer> {
    // JpaRepository已经提供了基础的CRUD方法
    
    /**
     * 根据ID查询产品，这是JPA自定义查询方法的命名约定
     * findBy + 属性名，JPA会自动实现该方法
     * @param pid 产品ID
     * @return 产品对象
     */
    Product findByPid(Integer pid);
} 