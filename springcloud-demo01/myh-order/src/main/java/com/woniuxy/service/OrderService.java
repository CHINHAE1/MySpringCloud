package com.woniuxy.service;

import com.woniuxy.entity.Order;
import com.woniuxy.entity.utils.ResponseMyEntity;

import java.util.List;

public interface OrderService {
    
    ResponseMyEntity createOrder(Order order) throws Exception;
    
    List<Order> findAll();
}
