package com.woniuxy.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

/**
 * OSS服务Feign客户端
 */
@FeignClient(value = "yr-oss")
public interface OssClient {

    /**
     * 上传文件
     *
     * @param file 文件
     * @return 上传结果
     */
    @PostMapping(value = "/oss/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    Object upload(@RequestPart("file") MultipartFile file);

    /**
     * 上传文件到指定目录
     *
     * @param file      文件
     * @param directory 目录
     * @return 上传结果
     */
    @PostMapping(value = "/oss/upload/{directory}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    Object uploadToDirectory(@RequestPart("file") MultipartFile file, @PathVariable("directory") String directory);

    /**
     * 删除文件
     *
     * @param fileName 文件名
     * @return 删除结果
     */
    @DeleteMapping("/oss/delete")
    Object delete(@RequestParam("fileName") String fileName);

    /**
     * 获取文件URL
     *
     * @param fileName 文件名
     * @return 文件URL
     */
    @GetMapping("/oss/url")
    Object getUrl(@RequestParam("fileName") String fileName);
} 