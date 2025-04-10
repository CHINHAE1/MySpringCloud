package com.woniuxy.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurationSupport;

import java.util.ArrayList;
import java.util.List;

/**
 * Web MVC配置类
 * 解决Spring MVC内容协商机制与配置冲突问题
 */
@Configuration
public class WebMvcConfig extends WebMvcConfigurationSupport {

    /**
     * 扩展消息转换器，保留原有转换器的同时添加对MediaType.ALL的支持
     */
    @Override
    protected void extendMessageConverters(List<HttpMessageConverter<?>> converters) {
        // 查找JSON转换器
        for (HttpMessageConverter<?> converter : converters) {
            if (converter instanceof MappingJackson2HttpMessageConverter) {
                MappingJackson2HttpMessageConverter jsonConverter = (MappingJackson2HttpMessageConverter) converter;
                
                // 获取支持的媒体类型列表
                List<MediaType> supportedMediaTypes = new ArrayList<>(jsonConverter.getSupportedMediaTypes());
                
                // 添加ALL类型支持
                if (!supportedMediaTypes.contains(MediaType.ALL)) {
                    supportedMediaTypes.add(MediaType.ALL);
                }
                
                // 设置更新后的媒体类型列表
                jsonConverter.setSupportedMediaTypes(supportedMediaTypes);
                break;
            }
        }
        
        super.extendMessageConverters(converters);
    }
} 