package com.woniuxy.controller;

import com.woniuxy.entity.Product;
import com.woniuxy.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 产品控制器
 */
@RestController
@RequestMapping("/product")
public class ProductController {
    
    @Value("${server.port}") 
    private String serverPort; // 使用@Value注解注入配置文件中的server.port值
    
    @Autowired
    private ProductService productService;
    
    /**
     * 查询所有产品
     * @return 产品列表
     */
    @GetMapping("/list")
    public List<Product> findAllProducts() {
        List<Product> products = productService.findAllProducts();
        // 为每个产品添加当前服务端口信息，用于验证负载均衡效果
        return products.stream().peek(product -> {
            // 将端口信息添加到产品名称中，这样客户端可以直观看到是哪个端口提供的服务
            product.setName(product.getName() + " (from port: " + serverPort + ")");
        }).collect(Collectors.toList()); // 使用Stream API修改返回结果
    }
    
    /**
     * 根据ID获取产品
     * @param pid 产品ID
     * @return 产品对象
     */
    @GetMapping("/{pid}")
    public Product getProductById(@PathVariable Integer pid) {
        Product product = productService.findProductById(pid);
        if (product != null) {
            // 添加当前服务端口信息
            product.setName(product.getName() + " (from port: " + serverPort + ")");
        }
        return product;
    }
    
    /**
     * 根据商品ID和数量扣减库存
     * @param pid 商品ID
     * @param num 扣减数量
     * @return 更新后的商品对象
     */
    @PostMapping("/deduct")
    public Product deductStock(@RequestParam Integer pid, @RequestParam Integer num) {
        Product product = productService.deductProductStock(pid, num);
        if (product != null) {
            // 添加当前服务端口信息
            product.setName(product.getName() + " (from port: " + serverPort + ")");
        }
        return product;
    }
} 