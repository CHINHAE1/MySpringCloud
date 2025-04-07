package com.woniuxy.feign;

import com.woniuxy.entity.Product;
import com.woniuxy.entity.utils.ResponseMyEntity;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.cloud.openfeign.SpringQueryMap;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * 利用OpenFeign来调用微服务，order-->product服务
 */
@FeignClient(value = "myh-product")
@RequestMapping("/product")  // 指定调用的微服务的接口
public interface ProductAPI {
    
    // 根据ID查询商品
    @GetMapping(value = {"/{pid}"})
    Product findAll(@PathVariable("pid") Integer pid);
    
    // 添加商品
    @PostMapping("/")
    ResponseMyEntity add(@RequestBody Product product);
    
    // 更新商品库存
    @PutMapping("/{pid}/{num}")
    ResponseMyEntity update(@PathVariable("pid") Integer pid, @PathVariable("num") Integer num);
    
    // 查询价格范围内的商品
    @GetMapping("/findAll")
    ResponseMyEntity findAll2(@SpringQueryMap Map<String, Double> map);
    
    // 根据商品属性查询
    @GetMapping("/findOne")
    ResponseMyEntity findProduct(@SpringQueryMap Product product);
    
    // 从ProductClient合并的方法
    @GetMapping("/findById/{pid}")
    Product findById(@PathVariable("pid") Integer pid);
    
    @PostMapping("/minusById/{pid}/{count}")
    void minusById(@PathVariable("pid") Integer pid, @PathVariable("count") Integer count);
} 