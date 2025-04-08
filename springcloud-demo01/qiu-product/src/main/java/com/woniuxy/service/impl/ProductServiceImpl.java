package com.woniuxy.service.impl;

import com.woniuxy.dao.ProductDao;
import com.woniuxy.entity.Product;
import com.woniuxy.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Service
public class ProductServiceImpl implements ProductService {
    @Autowired
    ProductDao productDao;
    @Override
    public List<Product> findAll() {
        return productDao.findAll();
    }

    @Override
    public Product save(Product product) {
        return productDao.save(product);
    }

    @Override
    public Product findProductByPid(Integer id) {
        return productDao.findProductByPid(id);
    }

    @Override
    public void deleteProductByPid(Integer pid) {
        productDao.deleteById(pid);
    }

    @Override
    public List<Product> findOne(String name, Double price, Integer stock) {
        // where name like xxx or price = xxx or stock = xxx
        List<Product> all  = productDao.findByNameLikeOrPriceOrStock(name,price,stock);
        return all;
    }
}
