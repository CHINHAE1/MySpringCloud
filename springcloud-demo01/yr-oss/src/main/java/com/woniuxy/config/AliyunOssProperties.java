package com.woniuxy.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * 阿里云OSS属性配置类
 */
@Data
@Component
@ConfigurationProperties(prefix = "aliyun.oss")
public class AliyunOssProperties {

    /**
     * 服务地址
     */
    private String endpoint;

    /**
     * 访问key
     */
    private String accessKeyId;

    /**
     * 访问密钥
     */
    private String accessKeySecret;

    /**
     * 存储桶名称
     */
    private String bucketName;

    /**
     * URL有效期(单位: 秒)
     */
    private Long expiration;

    /**
     * 上传文件大小限制(单位: MB)
     */
    private Integer maxSize;

    /**
     * 允许的文件类型
     */
    private String allowedExtensions;
} 