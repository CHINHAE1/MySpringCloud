package com.woniuxy.service.impl;

import com.aliyun.oss.OSS;
import com.aliyun.oss.OSSException;
import com.aliyun.oss.model.ObjectMetadata;
import com.aliyun.oss.model.PutObjectRequest;
import com.aliyun.oss.model.PutObjectResult;
import com.woniuxy.config.AliyunOssProperties;
import com.woniuxy.exception.OssException;
import com.woniuxy.model.dto.FileUploadResult;
import com.woniuxy.service.OssService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

/**
 * OSS服务实现类
 */
@Slf4j
@Service
public class OssServiceImpl implements OssService {

    @Autowired
    private OSS ossClient;

    @Autowired
    private AliyunOssProperties ossProperties;

    @Override
    public FileUploadResult uploadFile(MultipartFile file) {
        return uploadFile(file, "");
    }

    @Override
    public FileUploadResult uploadFile(InputStream inputStream, String fileName) {
        // 检查文件名
        if (StringUtils.isBlank(fileName)) {
            throw new OssException("文件名不能为空");
        }

        // 检查文件格式
        String extension = getFileExtension(fileName);
        checkFileExtension(extension);

        String objectName = generateObjectName("", fileName);

        // 创建上传请求
        PutObjectRequest putObjectRequest = new PutObjectRequest(ossProperties.getBucketName(), objectName, inputStream);

        // 设置文件元数据
        ObjectMetadata metadata = new ObjectMetadata();
        metadata.setContentType(getContentType(extension));
        putObjectRequest.setMetadata(metadata);

        try {
            // 上传文件
            PutObjectResult result = ossClient.putObject(putObjectRequest);
            log.info("文件上传成功，ETag: {}", result.getETag());

            // 获取文件URL
            String url = getFileUrl(objectName);

            // 构建上传结果
            return new FileUploadResult(
                    objectName,
                    fileName,
                    url,
                    extension,
                    0L, // 此处无法获取流的大小
                    LocalDateTime.now()
            );
        } catch (OSSException oe) {
            log.error("文件上传失败，错误信息: {}", oe.getMessage(), oe);
            throw new OssException("文件上传失败: " + oe.getMessage(), oe);
        } catch (Exception e) {
            log.error("文件上传失败，错误信息: {}", e.getMessage(), e);
            throw new OssException("文件上传失败: " + e.getMessage(), e);
        }
    }

    @Override
    public FileUploadResult uploadFile(MultipartFile file, String directory) {
        // 检查文件是否为空
        if (file == null || file.isEmpty()) {
            throw new OssException("上传文件不能为空");
        }

        // 获取原始文件名
        String originalFilename = file.getOriginalFilename();
        if (StringUtils.isBlank(originalFilename)) {
            throw new OssException("文件名不能为空");
        }

        // 检查文件大小
        long size = file.getSize();
        if (size > ossProperties.getMaxSize() * 1024 * 1024) {
            throw new OssException("文件大小超过限制，最大支持: " + ossProperties.getMaxSize() + "MB");
        }

        // 检查文件格式
        String extension = getFileExtension(originalFilename);
        checkFileExtension(extension);

        // 生成OSS对象名
        String objectName = generateObjectName(directory, originalFilename);

        try {
            // 获取文件输入流
            InputStream inputStream = file.getInputStream();

            // 创建上传请求
            PutObjectRequest putObjectRequest = new PutObjectRequest(ossProperties.getBucketName(), objectName, inputStream);

            // 设置文件元数据
            ObjectMetadata metadata = new ObjectMetadata();
            metadata.setContentType(getContentType(extension));
            metadata.setContentLength(size);
            putObjectRequest.setMetadata(metadata);

            // 上传文件
            PutObjectResult result = ossClient.putObject(putObjectRequest);
            log.info("文件上传成功，ETag: {}", result.getETag());

            // 获取文件URL
            String url = getFileUrl(objectName);

            // 构建上传结果
            return new FileUploadResult(
                    objectName,
                    originalFilename,
                    url,
                    extension,
                    size,
                    LocalDateTime.now()
            );
        } catch (IOException e) {
            log.error("文件上传失败，IO异常: {}", e.getMessage(), e);
            throw new OssException("文件上传失败: " + e.getMessage(), e);
        } catch (OSSException oe) {
            log.error("文件上传失败，OSS异常: {}", oe.getMessage(), oe);
            throw new OssException("文件上传失败: " + oe.getMessage(), oe);
        } catch (Exception e) {
            log.error("文件上传失败，异常信息: {}", e.getMessage(), e);
            throw new OssException("文件上传失败: " + e.getMessage(), e);
        }
    }

    @Override
    public List<FileUploadResult> uploadFiles(List<MultipartFile> files) {
        if (files == null || files.isEmpty()) {
            throw new OssException("上传文件列表不能为空");
        }

        return files.stream()
                .map(this::uploadFile)
                .collect(Collectors.toList());
    }

    @Override
    public boolean deleteFile(String fileName) {
        if (StringUtils.isBlank(fileName)) {
            throw new OssException("文件名不能为空");
        }

        try {
            ossClient.deleteObject(ossProperties.getBucketName(), fileName);
            log.info("文件删除成功: {}", fileName);
            return true;
        } catch (OSSException oe) {
            log.error("文件删除失败，OSS异常: {}", oe.getMessage(), oe);
            throw new OssException("文件删除失败: " + oe.getMessage(), oe);
        } catch (Exception e) {
            log.error("文件删除失败，异常信息: {}", e.getMessage(), e);
            throw new OssException("文件删除失败: " + e.getMessage(), e);
        }
    }

    @Override
    public String getFileUrl(String fileName) {
        if (StringUtils.isBlank(fileName)) {
            throw new OssException("文件名不能为空");
        }

        try {
            // 设置URL过期时间
            Date expiration = new Date(System.currentTimeMillis() + ossProperties.getExpiration() * 1000);
            URL url = ossClient.generatePresignedUrl(ossProperties.getBucketName(), fileName, expiration);
            return url.toString();
        } catch (OSSException oe) {
            log.error("获取文件URL失败，OSS异常: {}", oe.getMessage(), oe);
            throw new OssException("获取文件URL失败: " + oe.getMessage(), oe);
        } catch (Exception e) {
            log.error("获取文件URL失败，异常信息: {}", e.getMessage(), e);
            throw new OssException("获取文件URL失败: " + e.getMessage(), e);
        }
    }

    /**
     * 获取文件扩展名
     *
     * @param fileName 文件名
     * @return 扩展名
     */
    private String getFileExtension(String fileName) {
        return fileName.substring(fileName.lastIndexOf(".") + 1).toLowerCase();
    }

    /**
     * 检查文件扩展名是否允许
     *
     * @param extension 扩展名
     */
    private void checkFileExtension(String extension) {
        String[] allowedExtensions = ossProperties.getAllowedExtensions().split(",");
        boolean isAllowed = Arrays.asList(allowedExtensions).contains(extension.toLowerCase());
        if (!isAllowed) {
            throw new OssException("不支持的文件类型: " + extension);
        }
    }

    /**
     * 生成OSS对象名
     *
     * @param directory 目录
     * @param fileName  文件名
     * @return OSS对象名
     */
    private String generateObjectName(String directory, String fileName) {
        // 生成UUID作为文件名前缀，避免文件名冲突
        String uuid = UUID.randomUUID().toString().replaceAll("-", "");
        // 获取文件扩展名
        String extension = getFileExtension(fileName);
        // 构建对象名：目录 + UUID + 原始文件名(可选) + 扩展名
        StringBuilder objectName = new StringBuilder();
        
        // 添加目录
        if (StringUtils.isNotBlank(directory)) {
            if (!directory.endsWith("/")) {
                directory += "/";
            }
            objectName.append(directory);
        }
        
        // 添加UUID和扩展名
        objectName.append(uuid).append(".").append(extension);
        
        return objectName.toString();
    }

    /**
     * 获取文件的ContentType
     *
     * @param extension 文件扩展名
     * @return ContentType
     */
    private String getContentType(String extension) {
        // 常见文件类型映射
        Map<String, String> contentTypeMap = new HashMap<>();
        contentTypeMap.put("jpg", "image/jpeg");
        contentTypeMap.put("jpeg", "image/jpeg");
        contentTypeMap.put("png", "image/png");
        contentTypeMap.put("gif", "image/gif");
        contentTypeMap.put("bmp", "image/bmp");
        contentTypeMap.put("pdf", "application/pdf");
        contentTypeMap.put("doc", "application/msword");
        contentTypeMap.put("docx", "application/vnd.openxmlformats-officedocument.wordprocessingml.document");
        contentTypeMap.put("xls", "application/vnd.ms-excel");
        contentTypeMap.put("xlsx", "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        contentTypeMap.put("txt", "text/plain");
        contentTypeMap.put("zip", "application/zip");
        contentTypeMap.put("rar", "application/x-rar-compressed");
        
        return contentTypeMap.getOrDefault(extension.toLowerCase(), "application/octet-stream");
    }
} 