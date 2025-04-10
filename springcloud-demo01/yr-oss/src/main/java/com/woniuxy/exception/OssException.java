package com.woniuxy.exception;

/**
 * OSS服务异常类
 */
public class OssException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    private int code;

    public OssException(String message) {
        super(message);
        this.code = 500;
    }

    public OssException(String message, Throwable cause) {
        super(message, cause);
        this.code = 500;
    }

    public OssException(int code, String message) {
        super(message);
        this.code = code;
    }

    public OssException(int code, String message, Throwable cause) {
        super(message, cause);
        this.code = code;
    }

    public int getCode() {
        return code;
    }
} 