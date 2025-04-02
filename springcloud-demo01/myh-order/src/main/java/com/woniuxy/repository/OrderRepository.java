package com.woniuxy.repository;

import com.woniuxy.entity.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * 订单数据访问层
 */
@Repository
public interface OrderRepository extends JpaRepository<Order, Integer> {
    // JpaRepository已经提供了基础的CRUD方法
} 