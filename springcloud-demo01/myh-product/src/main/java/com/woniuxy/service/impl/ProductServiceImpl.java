package com.woniuxy.service.Impl;

import com.woniuxy.dao.ProductDao;
import com.woniuxy.entity.Product;
import com.woniuxy.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class ProductServiceImpl implements ProductService {

    @Autowired
    private ProductDao productDao;

    @Override
    public Product findById(Integer pid) {
        return productDao.findById(pid).orElse(null);
    }

    @Override
    @Transactional
    public void save(Product product) {
        productDao.save(product);
    }

    @Override
    @Transactional
    public int update(Integer pid, Integer num) {
        return productDao.updateStockById(pid, num);
    }

    @Override
    public List<Product> findByPrice(double startPrice, double endPrice) {
        return productDao.findAllByPriceBetween(startPrice, endPrice);
    }

    @Override
    public List<Product> findOne(String name, double price, Integer stock) {
        return productDao.findByNameLikeOrPriceOrStock(name, price, stock);
    }

    @Override
    @Transactional
    public void minusById(Integer pid, Integer count) {
        Product product = productDao.findById(pid).orElse(null);
        if (product != null) {
            product.setStock(product.getStock() - count);
            productDao.save(product);
        }
    }
} 