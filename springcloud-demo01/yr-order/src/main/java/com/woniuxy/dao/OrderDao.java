package com.woniuxy.dao;

import com.woniuxy.entity.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.http.ResponseEntity;

public interface OrderDao extends JpaRepository<Order,Integer> {

}
