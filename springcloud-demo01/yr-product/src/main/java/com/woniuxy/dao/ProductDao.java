package com.woniuxy.dao;

import com.woniuxy.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ProductDao extends JpaRepository<Product,Integer> {
    List<Product> findAll();
    Product save(Product product);
    Product findProductByPid(Integer pid);
//    void deleteProductByPid(Integer pid);
    List<Product> findByNameLikeOrPriceOrStock(String name,Double price,Integer stock);
    
    /**
     * 根据库存范围查询商品
     * @param minStock 最小库存值（包含）
     * @param maxStock 最大库存值（包含）
     * @return 符合库存范围的商品列表
     */
    @Query("SELECT p FROM myh_product p WHERE p.stock >= :minStock AND p.stock <= :maxStock")
    List<Product> findByStockBetween(@Param("minStock") Integer minStock, @Param("maxStock") Integer maxStock);
    
    /**
     * 查询指定库存值的商品
     * @param stock 库存值
     * @return 符合条件的商品列表
     */
    List<Product> findByStock(Integer stock);
}
