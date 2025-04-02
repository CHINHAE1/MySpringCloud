package com.woniuxy.controller;

import com.woniuxy.entity.Order;
import com.woniuxy.entity.Product;
import com.woniuxy.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 订单控制器
 */
@RestController
@RequestMapping("/order")
public class OrderController {
    
    @Autowired
    private OrderService orderService;
    
    /**
     * 查询所有产品
     * @return 产品列表
     */
    @GetMapping("/products")
    public List<Product> getProductList() {
        return orderService.findAllProducts();
    }
    
    /**
     * 创建订单
     * @param uid 用户ID
     * @param pid 商品ID
     * @param number 购买数量
     * @return 创建结果
     * @throws Exception 创建失败时抛出异常
     */
    @PostMapping("/create")
    public String createOrder(
            @RequestParam Integer uid,
            @RequestParam Integer pid,
            @RequestParam Integer number) throws Exception {
        return orderService.createOrder(uid, pid, number);
    }
} 