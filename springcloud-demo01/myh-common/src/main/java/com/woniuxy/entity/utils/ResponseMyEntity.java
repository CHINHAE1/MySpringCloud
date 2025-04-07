package com.woniuxy.entity.utils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

import java.io.Serializable;

/**
 * 统一响应对象
 */
public class ResponseMyEntity implements Serializable {
    
    private static final long serialVersionUID = 1L;
    
    private JSONObject data = new JSONObject();
    
    public ResponseMyEntity() {
        this.data.put("code", 200);
        this.data.put("msg", "操作成功");
    }
    
    public ResponseMyEntity(Object res) {
        this();
        this.data.put("data", res);
    }
    
    public ResponseMyEntity(int code, String msg) {
        this.data.put("code", code);
        this.data.put("msg", msg);
    }
    
    public ResponseMyEntity(int code, String msg, Object res) {
        this.data.put("code", code);
        this.data.put("msg", msg);
        this.data.put("data", res);
    }
    
    public void put(String key, Object value) {
        this.data.put(key, value);
    }
    
    public Object get(String key) {
        return this.data.get(key);
    }
    
    @Override
    public String toString() {
        return JSON.toJSONString(this.data);
    }
} 