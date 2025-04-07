package com.woniuxy.dao;

import com.woniuxy.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface ProductDao extends JpaRepository<Product, Integer> {
    
    List<Product> findAllByPriceBetween(double startPrice, double endPrice);
    
    List<Product> findByNameLikeOrPriceOrStock(String name, double price, Integer stock);
    
    @Modifying
    @Transactional
    @Query("update Product p set p.stock = p.stock - ?2 where p.id = ?1")
    int updateStockById(Integer id, Integer num);
}