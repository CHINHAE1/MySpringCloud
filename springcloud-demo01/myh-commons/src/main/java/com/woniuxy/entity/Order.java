package com.woniuxy.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import java.io.Serializable;

/**
 * @Author: chinhae
 * @Do: 订单
 * @DateTime: 22/05/30/0030 上午 11:36
 * @Component: 成都蜗牛学苑
 **/
@Entity(name = "myh_order")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Order implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer oid;
    private Integer uid;//用户id
    private String username;//用户名
    private Integer pid;//商品id
    private String pname;//商品名称
    private Double price;//商品价格
    private Integer number;//购买数量
}