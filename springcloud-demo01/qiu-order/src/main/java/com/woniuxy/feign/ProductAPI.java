package com.woniuxy.feign;

import com.woniuxy.entity.Product;
import com.woniuxy.entity.utils.ResponseMyEntity;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.cloud.openfeign.SpringQueryMap;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@FeignClient(value = "yr-product")
@RequestMapping("/product")
public interface ProductAPI {
    @GetMapping("/")
    public ResponseMyEntity findAll();
    @GetMapping("/{pid}")
    public Product findById(@PathVariable("pid") Integer pid);
    @PostMapping("/{pid}/{number}")
    public ResponseMyEntity save(@PathVariable("pid") Integer pid,@PathVariable("number") Integer number);
    @GetMapping("/findOne")
    public ResponseMyEntity findProduct(@SpringQueryMap Product Product);
}
