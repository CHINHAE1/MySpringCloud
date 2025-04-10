package com.woniuxy.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 文件上传结果DTO
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class FileUploadResult {

    /**
     * 文件名
     */
    private String fileName;

    /**
     * 原始文件名
     */
    private String originalFileName;

    /**
     * 文件URL
     */
    private String url;

    /**
     * 文件类型
     */
    private String fileType;

    /**
     * 文件大小(字节)
     */
    private Long fileSize;

    /**
     * 上传时间
     */
    private LocalDateTime uploadTime;
} 