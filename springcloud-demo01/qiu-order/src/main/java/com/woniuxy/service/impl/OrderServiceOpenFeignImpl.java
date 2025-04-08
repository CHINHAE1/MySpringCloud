package com.woniuxy.service.impl;

import com.woniuxy.dao.OrderDao;
import com.woniuxy.entity.Order;
import com.woniuxy.entity.Product;
import com.woniuxy.entity.utils.ResponseMyEntity;
import com.woniuxy.feign.ProductAPI;
import com.woniuxy.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class OrderServiceOpenFeignImpl implements OrderService{
    @Autowired
    private OrderDao orderDao;
    @Autowired
    private ProductAPI productAPI;
    @Override
    public ResponseMyEntity createOrder(Order order) throws RuntimeException {
                    //拿到对应商品
        Product product = null;
        try {
            product = productAPI.findById(order.getPid());
        } catch (Exception e) {
            throw new RuntimeException("调用product微服务超时");
        }
        //商品库存
            Integer stock=product.getStock();
            Integer i=order.getNumber();
            Integer x= (stock-i);
            if(x>=0){
                order.setPname(product.getName());
                order.setPrice(product.getPrice());
                orderDao.save(order);
                ResponseMyEntity responseMyEntity=productAPI.save(order.getPid(),order.getNumber());
                return responseMyEntity;
            }else{
                return null;
            }
    }
}
