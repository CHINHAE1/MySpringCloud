package com.woniuxy.service;

import com.woniuxy.model.dto.FileUploadResult;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.util.List;

/**
 * OSS服务接口
 */
public interface OssService {

    /**
     * 上传文件
     *
     * @param file 文件
     * @return 文件上传结果
     */
    FileUploadResult uploadFile(MultipartFile file);

    /**
     * 上传文件
     *
     * @param file     文件流
     * @param fileName 文件名
     * @return 文件上传结果
     */
    FileUploadResult uploadFile(InputStream file, String fileName);

    /**
     * 上传文件到指定目录
     *
     * @param file      文件
     * @param directory 目录
     * @return 文件上传结果
     */
    FileUploadResult uploadFile(MultipartFile file, String directory);

    /**
     * 批量上传文件
     *
     * @param files 文件列表
     * @return 文件上传结果列表
     */
    List<FileUploadResult> uploadFiles(List<MultipartFile> files);

    /**
     * 删除文件
     *
     * @param fileName 文件名
     * @return 是否删除成功
     */
    boolean deleteFile(String fileName);

    /**
     * 获取文件访问URL
     *
     * @param fileName 文件名
     * @return 文件访问URL
     */
    String getFileUrl(String fileName);
} 