package com.woniuxy.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import java.io.Serializable;

//Entity可以根据下面的实体类，自动生成数据库的表单  jpa的功能：hibernate的规范化实现方案。完全的ORM（对象关系映射）框架
@Entity(name = "myh_user")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class User implements Serializable {
    @Id  //表示uid是主键
    @GeneratedValue(strategy = GenerationType.AUTO) //表示主键自增
    private Integer uid;
    String username;
    String password;
    String telphone;
}
