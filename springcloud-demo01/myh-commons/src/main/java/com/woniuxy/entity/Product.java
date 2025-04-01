package com.woniuxy.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Generated;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import java.io.Serializable;

/**
 * @Author: chinhae
 * @Do: 商品
 * @DateTime: 22/05/30/0030 上午 11:34
 * @Component: 成都蜗牛学苑
 **/
@Entity(name = "myh_product")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Product implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer pid;
    private String name;
    private  Double price;
    private Integer stock;
}