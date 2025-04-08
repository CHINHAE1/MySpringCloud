package com.woniuxy.controller;

import com.woniuxy.entity.Product;
import com.woniuxy.entity.utils.ResponseMyEntity;
import com.woniuxy.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.cloud.openfeign.SpringQueryMap;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RefreshScope
@RestController
@RequestMapping("/product")
public class ProductController {
    @Autowired
    ProductService productService;
    @Value("yr-product")
    String service_name;
    @Value("${PORT:8081}")
    int port;
    @Value("${product.name}")
    String product_name;
    @GetMapping("/")
    public ResponseMyEntity findAll(){
//        for(int i=0;i<10;i++){
//            Product product=new Product();
//            product.setName("蜗牛商品"+i);
//            product.setPrice(1000.0+i);
//            product.setStock(100);
//            productService.save(product);
//        }
        ResponseMyEntity responseMyEntity=new ResponseMyEntity();
        List<Product> productList=productService.findAll();
        responseMyEntity.put("data",productList);
        responseMyEntity.put("product_name",product_name);
        return responseMyEntity;
    }
    static int i=0;
    //根据id查product
    @GetMapping("/{pid}")
    public Product findById(@PathVariable Integer pid){
        if ((i++)%4==0){
            throw new RuntimeException("模拟异常");
        }
        return productService.findProductByPid(pid);
    }
//    根据pid和number修改库存
    @PostMapping("/{pid}/{number}")
    public ResponseMyEntity save(@PathVariable Integer pid,@PathVariable Integer number){
        ResponseMyEntity responseMyEntity=new ResponseMyEntity();
        Product product=productService.findProductByPid(pid);
        Integer stock=product.getStock();
        Integer stock1= (Integer) (stock-number);
        product.setStock(stock1);
        responseMyEntity.put("data",productService.save(product));
        responseMyEntity.put("msg",service_name+":"+port);
        return responseMyEntity;
    }
    //根据id删除
    @DeleteMapping("/delete/{pid}")
    public ResponseMyEntity delete(@PathVariable Integer pid){
        ResponseMyEntity responseMyEntity=new ResponseMyEntity();
        productService.deleteProductByPid(pid);
        return responseMyEntity.SUCCESS;
    }
    //条件查询
    @GetMapping("/findOne")
    public ResponseMyEntity findProduct(@RequestParam String name,@RequestParam Double price,@RequestParam Integer stock) {
        List<Product> all = productService.findOne(name, price, stock);
        return new ResponseMyEntity(all);
    }
}
