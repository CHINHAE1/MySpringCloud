package com.woniuxy.controller;

import com.alibaba.csp.sentinel.annotation.SentinelResource;
import com.alibaba.csp.sentinel.slots.block.BlockException;
import com.woniuxy.entity.Order;
import com.woniuxy.entity.utils.ResponseMyEntity;
import com.woniuxy.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/order")
@RefreshScope // 动态刷新配置，不用重启
public class OrderController {
    @Autowired
    OrderService orderService;
    
    @Value("${product.myname:默认值}")
    private String myname;

    /**
     * 创建商品订单
     * @param order 订单信息
     * @return 响应结果
     */
    @PostMapping("/")
    // 设置fallback降级方法和blockHandler熔断方法
    @SentinelResource(value = "createOrder", fallback = "fallback", blockHandler = "blockMethod")
    public ResponseMyEntity createOrder(@RequestBody Order order) throws Exception {
        return orderService.createOrder(order);
    }
    
    /**
     * 降级方法
     * @param order 订单信息
     * @param e 异常
     * @return 降级响应
     */
    public ResponseMyEntity fallback(@RequestBody Order order, Throwable e) {
        return new ResponseMyEntity(200, e.getMessage() + "服务器正在维护中...");
    }
    
    /**
     * 熔断方法
     * @param order 订单信息
     * @param e 熔断异常
     * @return 熔断响应
     */
    public ResponseMyEntity blockMethod(@RequestBody Order order, BlockException e) {
        return new ResponseMyEntity(200, e.getMessage() + "服务器正在维护中...");
    }
    
    /**
     * 获取配置信息
     * @return 配置值
     */
    @GetMapping("/config")
    public String getConfig() {
        return myname;
    }
}
