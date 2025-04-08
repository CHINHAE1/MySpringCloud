package com.woniuxy.entity.utils;

import org.springframework.http.HttpStatus;

import java.util.HashMap;

public class ResponseMyEntity extends HashMap {
    //枚举类，用于定义ResponseEntity的key
    public enum ResponseStatus{
        //code 响应编码
        CODE("code"),
        //message 响应消息
        MSG("msg"),
        //data响应体
        DATA("data");
        private final String value;
        ResponseStatus(String value) {
            this.value = value;
        }
        public String value(){
            return this.value;
        }
    }
    private static final String CODE=ResponseStatus.CODE.value();//当做map中的key
    private static final String MSG=ResponseStatus.MSG.value;
    private static final String OBJ = ResponseStatus.DATA.value();
    public ResponseMyEntity(){

    }
    public ResponseMyEntity(Object obj) {
        super.put(CODE, HttpStatus.OK.value());
        super.put(MSG,"执行成功");
        super.put(OBJ,obj);
    }
    public ResponseMyEntity(int code, String message ){
        super.put(CODE,code);
        super.put(MSG,message);
    }
    public ResponseMyEntity(int code, String message, Object data) {
        super.put(CODE,code);
        super.put(MSG,message);
        if (data != null) {
            super.put(OBJ,data);
        }
    }

    public static final ResponseMyEntity SUCCESS=new ResponseMyEntity(HttpStatus.OK.value(),"执行成功！");
    public static final ResponseMyEntity ERROR=new ResponseMyEntity(HttpStatus.INTERNAL_SERVER_ERROR.value(),"执行失败！");

    public ResponseMyEntity putKey(String key, Object obj){
        super.put(key,obj);
        return  this;
    }
}
