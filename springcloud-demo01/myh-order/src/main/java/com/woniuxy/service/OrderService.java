package com.woniuxy.service;

import com.woniuxy.entity.Order;
import com.woniuxy.entity.Product;

import java.util.List;

/**
 * 订单服务接口
 */
public interface OrderService {
    
    /**
     * 查询所有产品
     * @return 所有产品列表
     */
    List<Product> findAllProducts();
    
    /**
     * 创建订单
     * @param uid 用户ID
     * @param pid 商品ID
     * @param number 购买数量
     * @return 创建结果，成功返回success
     * @throws Exception 创建失败时抛出异常
     */
    String createOrder(Integer uid, Integer pid, Integer number) throws Exception;
} 