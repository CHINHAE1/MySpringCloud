package com.woniuxy.controller;

import com.woniuxy.entity.Product;
import com.woniuxy.entity.utils.ResponseMyEntity;
import com.woniuxy.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/product")
public class ProductController {

    @Autowired
    ProductService productService;
    
    //读取yml中的server.port配置的值
    @Value("${server.port}")
    private String port;

    /**
     * 根据pid搜索对应的商品信息，直接返回product
     * @param pid
     * @return
     */
    @GetMapping(value = {"/{pid}"})
    public Product findAll(@PathVariable Integer pid) {
        // 可以添加模拟异常测试熔断降级
        // if ((i++) % 4== 0) {
        //     throw new RuntimeException("模拟异常");
        // }
        // 可以添加延时测试超时
        // try {
        //     Thread.sleep(2000);
        // } catch (InterruptedException e) {
        //     e.printStackTrace();
        // }
        return productService.findById(pid);
    }
    
    /**
     * 兼容旧版接口 - 根据ID查询商品
     */
    @GetMapping("/findById/{pid}")
    public Product findById(@PathVariable("pid") Integer pid) {
        return productService.findById(pid);
    }
    
    /**
     * 兼容旧版接口 - 减少商品库存
     */
    @PostMapping("/minusById/{pid}/{count}")
    public void minusById(@PathVariable("pid") Integer pid, @PathVariable("count") Integer count) {
        productService.minusById(pid, count);
    }

    /**
     * 添加商品
     * @param product
     * @return
     */
    @PostMapping("/")
    public ResponseMyEntity add(@RequestBody Product product) {
        productService.save(product);
        return new ResponseMyEntity(200, "添加成功");
    }

    /**
     * 根据商品Id,扣除商品数量
     * @param pid
     * @param num
     * @return
     */
    @PutMapping("/{pid}/{num}")
    public ResponseMyEntity update(@PathVariable Integer pid, @PathVariable Integer num) {
        int result = productService.update(pid, num);
        ResponseMyEntity responseMyEntity = new ResponseMyEntity(result);
        responseMyEntity.put("port", port);
        return responseMyEntity;
    }
    
    /**
     * 查询一个价格范围内的商品，/findAll?startPrice=100&endPrice=200
     * @param startPrice
     * @param endPrice
     * @return
     */
    @GetMapping("/findAll")
    public ResponseMyEntity findAll(@RequestParam double startPrice, @RequestParam double endPrice) {
        return new ResponseMyEntity(productService.findByPrice(startPrice, endPrice));
    }
    
    /**
     * 根据实体类Product的属性来查询商品信息
     * /findOne?name=xxx&price=xxx&stock=xxx
     * @param name 模糊查询
     * @param price
     * @param stock
     * @return
     */
    @GetMapping("/findOne")
    public ResponseMyEntity findProduct(@RequestParam String name, @RequestParam double price, @RequestParam Integer stock) {
        return new ResponseMyEntity(productService.findOne(name, price, stock));
    }
}
