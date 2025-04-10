package com.woniuxy.controller;

import com.woniuxy.model.dto.FileUploadResult;
import com.woniuxy.model.vo.ResponseResult;
import com.woniuxy.service.OssService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 * OSS文件控制器
 */
@Slf4j
@RestController
@RequestMapping("/oss")
public class OssController {

    @Autowired
    private OssService ossService;

    /**
     * 上传单个文件
     *
     * @param file 文件
     * @return 上传结果
     */
    @PostMapping("/upload")
    public ResponseResult<FileUploadResult> upload(@RequestParam("file") MultipartFile file) {
        log.info("接收到文件上传请求，文件名: {}, 大小: {}", file.getOriginalFilename(), file.getSize());
        FileUploadResult result = ossService.uploadFile(file);
        return ResponseResult.success(result);
    }

    /**
     * 上传文件到指定目录
     *
     * @param file      文件
     * @param directory 目录
     * @return 上传结果
     */
    @PostMapping("/upload/{directory}")
    public ResponseResult<FileUploadResult> uploadToDirectory(
            @RequestParam("file") MultipartFile file,
            @PathVariable("directory") String directory) {
        log.info("接收到文件上传请求，文件名: {}, 大小: {}, 目录: {}", 
                file.getOriginalFilename(), file.getSize(), directory);
        FileUploadResult result = ossService.uploadFile(file, directory);
        return ResponseResult.success(result);
    }

    /**
     * 上传多个文件
     *
     * @param files 文件列表
     * @return 上传结果列表
     */
    @PostMapping("/uploads")
    public ResponseResult<List<FileUploadResult>> uploadBatch(@RequestParam("files") List<MultipartFile> files) {
        log.info("接收到批量文件上传请求，文件数量: {}", files.size());
        List<FileUploadResult> results = ossService.uploadFiles(files);
        return ResponseResult.success(results);
    }

    /**
     * 删除文件
     *
     * @param fileName 文件名
     * @return 删除结果
     */
    @DeleteMapping("/delete")
    public ResponseResult<Boolean> delete(@RequestParam("fileName") String fileName) {
        log.info("接收到文件删除请求，文件名: {}", fileName);
        boolean result = ossService.deleteFile(fileName);
        return ResponseResult.success(result);
    }

    /**
     * 获取文件URL
     *
     * @param fileName 文件名
     * @return 文件URL
     */
    @GetMapping("/url")
    public ResponseResult<String> getUrl(@RequestParam("fileName") String fileName) {
        log.info("接收到获取文件URL请求，文件名: {}", fileName);
        String url = ossService.getFileUrl(fileName);
        return ResponseResult.success(url);
    }
} 