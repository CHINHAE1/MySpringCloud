//package com.woniuxy.service.impl;
//
//import com.woniuxy.dao.OrderDao;
//import com.woniuxy.entity.Order;
//import com.woniuxy.entity.Product;
//import com.woniuxy.entity.utils.ResponseMyEntity;
//import com.woniuxy.service.OrderService;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Service;
//import org.springframework.web.client.RestTemplate;
//
//
//@Service
//public class OrderServiceImpl implements OrderService  {
//    @Autowired
//    private OrderDao orderDao;
//    @Autowired
//    RestTemplate restTemplate;
//    @Override
//    public ResponseMyEntity createOrder(Order order) {
//            //拿到对应商品
//            Product product = restTemplate.getForEntity("http://yr-product/product/" + order.getPid(),
//                    Product.class).getBody();
//            //商品库存
//            Integer stock=product.getStock();
//            Integer i=order.getNumber();
//            Integer x= (stock-i);
//            if(x>=0){
//                order.setPname(product.getName());
//                order.setPrice(product.getPrice());
//                orderDao.save(order);
//                ResponseMyEntity responseMyEntity=restTemplate.postForObject("http://yr-product/product/"+order.getPid()+"/"+order.getNumber(),null,ResponseMyEntity.class);
//                return responseMyEntity;
//            }else{
//                return null;
//            }
//    }
//}
