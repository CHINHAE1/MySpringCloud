package com.woniuxy.anno;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @Author: 马宇航
 * @Description: 自定义权限注解
 * @DateTime: 25/04/09/星期三 17:33
 * @Component: 成都蜗牛学苑
 **/
//表示，该注解，可以加到方法上和类上
@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME) //运行时生效
public @interface PermAccess {
    //权限的标记字段 order::add
    String value() default "";
} 