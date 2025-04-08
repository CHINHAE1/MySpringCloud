package com.woniuxy.service;

import com.woniuxy.entity.Order;
import com.woniuxy.entity.utils.ResponseMyEntity;
import org.springframework.http.ResponseEntity;

public interface OrderService {
     ResponseMyEntity createOrder(Order order) throws RuntimeException;
}
