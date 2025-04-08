package com.woniuxy.controller;

import com.woniuxy.entity.Order;
import com.woniuxy.entity.utils.ResponseMyEntity;
import org.springframework.web.bind.annotation.RequestBody;

/**
 *  OrderController的降级类
 *  降级处理类中的对应方法，必须是静态方法，否则没法解析
 */
public class OrderControllerFallback {
    public static ResponseMyEntity fallback(@RequestBody Order order, Throwable e){
        return new ResponseMyEntity(200,e.getMessage()+"方法降级服务器正在维护中...");
    }
}
