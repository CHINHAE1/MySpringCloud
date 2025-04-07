package com.woniuxy.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.io.Serializable;

/**
 * @Author: 马宇航
 * @Do: 订单
 * @DateTime: 22/05/30/0030 上午 11:36
 * @Component: 成都蜗牛学苑
 **/
@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "t_order")
public class Order implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    
    private Integer uid;
    
    private Integer pid;
    
    private Integer number;
}