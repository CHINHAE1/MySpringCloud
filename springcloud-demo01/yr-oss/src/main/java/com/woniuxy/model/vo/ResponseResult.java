package com.woniuxy.model.vo;

import lombok.Data;

import java.io.Serializable;

/**
 * 统一返回结果
 */
@Data
public class ResponseResult<T> implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 状态码
     */
    private int code;

    /**
     * 返回消息
     */
    private String message;

    /**
     * 返回数据
     */
    private T data;

    /**
     * 成功返回结果
     */
    public static <T> ResponseResult<T> success() {
        return success(null);
    }

    /**
     * 成功返回结果
     *
     * @param data 返回数据
     */
    public static <T> ResponseResult<T> success(T data) {
        return success(200, "操作成功", data);
    }

    /**
     * 成功返回结果
     *
     * @param code    状态码
     * @param message 提示信息
     * @param data    返回数据
     */
    public static <T> ResponseResult<T> success(int code, String message, T data) {
        ResponseResult<T> result = new ResponseResult<>();
        result.setCode(code);
        result.setMessage(message);
        result.setData(data);
        return result;
    }

    /**
     * 失败返回结果
     */
    public static <T> ResponseResult<T> error() {
        return error(500, "操作失败");
    }

    /**
     * 失败返回结果
     *
     * @param message 提示信息
     */
    public static <T> ResponseResult<T> error(String message) {
        return error(500, message);
    }

    /**
     * 失败返回结果
     *
     * @param code    状态码
     * @param message 提示信息
     */
    public static <T> ResponseResult<T> error(int code, String message) {
        return success(code, message, null);
    }
} 