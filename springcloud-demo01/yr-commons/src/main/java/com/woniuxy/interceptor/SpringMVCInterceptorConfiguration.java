package com.woniuxy.interceptor;

import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * @Author: 马宇航
 * @Todo: 拦截器配置类
 * @DateTime: 25/04/09/星期三 17:40
 * @Component: 成都蜗牛学苑
 **/
@Configuration
@ConditionalOnWebApplication(type = ConditionalOnWebApplication.Type.SERVLET)
public class SpringMVCInterceptorConfiguration implements WebMvcConfigurer {
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new PermInterceptor())
                .addPathPatterns("/**")
                .excludePathPatterns("/login", "/auth/login", "/auth/register")
                .order(1); //拦截器的执行顺序
    }
} 