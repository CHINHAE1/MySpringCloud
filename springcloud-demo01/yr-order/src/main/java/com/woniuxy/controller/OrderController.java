package com.woniuxy.controller;

import com.alibaba.csp.sentinel.annotation.SentinelResource;
import com.alibaba.csp.sentinel.slots.block.BlockException;
import com.woniuxy.entity.Order;
import com.woniuxy.entity.utils.ResponseMyEntity;
import com.woniuxy.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.web.bind.annotation.*;
@RefreshScope
@RestController
@RequestMapping("/order")
public class OrderController {
@Autowired
    private OrderService orderService;


//    @PostMapping("/")
////  设置fallback降级方法
////  方案1.@SentinelResource(value = "createOrder",fallback = "fallback") 后面的fallback方法，在同一个类里面
////  方案2.如果降级方法，不是在同一个类中，还需要配置fallbackClass,降级处理类中的对应方法，必须是静态方法，否则没法解析
////  OrderController的降级类是OrderControllerFallback
//
//    @SentinelResource(value = "creatOrder",fallback = "fallback",fallbackClass = OrderControllerFallback.class)
//    public ResponseMyEntity creatOrder(@RequestBody Order order) throws Exception{
//    ResponseMyEntity resp = new ResponseMyEntity();
//    resp.put("data",orderService.createOrder(order));
//    return resp;
//}
////    public ResponseMyEntity fallback(@RequestBody Order order,Throwable e){
////        return new ResponseMyEntity(200,e.getMessage()+"服务器正在维护中...");
////    }


    @PostMapping("/")
    //设置熔断器
    // 基本上性能是稳定了，可以快速失败（熔断的效果），熔断时长，不接受正常进入到createOrder的请求，直接响应200，进入到我们的熔断的方法中。
    @SentinelResource(value = "creatOrder",blockHandler = "blockMethod")
    public ResponseMyEntity creatOrder(@RequestBody Order order) throws Exception{
        ResponseMyEntity resp = new ResponseMyEntity();
        resp.put("data",orderService.createOrder(order));
        return resp;
    }
    public ResponseMyEntity blockMethod(@RequestBody Order order, BlockException blockException) {
        return new ResponseMyEntity(200,blockException.getMessage()+"方法熔断，服务器正在维护中...");
    }
}
