package com.woniuxy.service.Impl;

import com.woniuxy.dao.OrderDao;
import com.woniuxy.entity.Order;
import com.woniuxy.entity.Product;
import com.woniuxy.entity.utils.ResponseMyEntity;
import com.woniuxy.feign.ProductAPI;
import com.woniuxy.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;

@Service
public class OrderServiceImpl implements OrderService {
    
    @Autowired
    private OrderDao orderDao;
    
    @Autowired
    private ProductAPI productAPI;
    
    @Override
    public List<Order> findAll() {
        return orderDao.findAll();
    }
    
    @Override
    public ResponseMyEntity createOrder(Order order) throws Exception {
        // 1. 通过OpenFeign调用product服务查询商品信息
        Product product = productAPI.findAll(order.getPid());
        
        // 2. 判断库存数量是否足够
        int remainingStock = product.getStock() - order.getNumber();
        if (remainingStock < 0) {
            return new ResponseMyEntity(201, "库存不足");
        }
        
        // 3. 通过OpenFeign调用product服务更新库存
        ResponseMyEntity updateResult = productAPI.update(order.getPid(), order.getNumber());
        
        // 4. 保存订单
        orderDao.save(order);
        
        // 5. 返回结果
        return updateResult;
    }
}
