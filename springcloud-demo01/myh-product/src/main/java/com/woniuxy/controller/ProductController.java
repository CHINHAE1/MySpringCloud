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
    private String serverPort;
    
    @Autowired
    private ProductService productService;
    
    /**
     * 查询所有产品
     * @return 产品列表
     */
    @GetMapping("/list")
    public List<Product> findAllProducts() {
        List<Product> products = productService.findAllProducts();
        // 为每个产品添加当前服务端口信息
        return products.stream().peek(product -> {
            product.setName(product.getName() + " (from port: " + serverPort + ")");
        }).collect(Collectors.toList());
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