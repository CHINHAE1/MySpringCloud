package com.woniuxy.dao;

import com.woniuxy.entity.Order;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderDao extends JpaRepository<Order, Integer> {
}
