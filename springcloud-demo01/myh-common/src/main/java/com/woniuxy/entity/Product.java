package com.woniuxy.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.io.Serializable;

/**
 * @Author: 马宇航
 * @Do: 商品
 * @DateTime: 22/05/30/0030 上午 11:34
 * @Component: 成都蜗牛学苑
 **/
@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "t_product")
public class Product implements Serializable {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    
    private String name;
    
    private Double price;
    
    private Integer stock;
}