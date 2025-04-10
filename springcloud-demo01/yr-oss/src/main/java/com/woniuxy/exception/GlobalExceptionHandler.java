package com.woniuxy.exception;

import com.woniuxy.model.vo.ResponseResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.multipart.MaxUploadSizeExceededException;

/**
 * 全局异常处理类
 */
@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * 处理OSS异常
     */
    @ExceptionHandler(OssException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ResponseResult<String> handleOssException(OssException e) {
        log.error("OSS异常: {}", e.getMessage(), e);
        return ResponseResult.error(e.getCode(), e.getMessage());
    }

    /**
     * 处理文件大小超出限制异常
     */
    @ExceptionHandler(MaxUploadSizeExceededException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseResult<String> handleMaxUploadSizeExceededException(MaxUploadSizeExceededException e) {
        log.error("文件大小超出限制: {}", e.getMessage(), e);
        return ResponseResult.error(400, "文件大小超出限制");
    }

    /**
     * 处理其他异常
     */
    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ResponseResult<String> handleException(Exception e) {
        log.error("系统异常: {}", e.getMessage(), e);
        return ResponseResult.error(500, "系统异常: " + e.getMessage());
    }
} 