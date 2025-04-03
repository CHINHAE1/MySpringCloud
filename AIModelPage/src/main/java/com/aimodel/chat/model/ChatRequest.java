package com.aimodel.chat.model;

/**
 * 聊天请求模型类
 */
public class ChatRequest {
    
    private String message;
    private String modelType;
    
    public ChatRequest() {
    }
    
    public ChatRequest(String message, String modelType) {
        this.message = message;
        this.modelType = modelType;
    }
    
    public String getMessage() {
        return message;
    }
    
    public void setMessage(String message) {
        this.message = message;
    }
    
    public String getModelType() {
        return modelType;
    }
    
    public void setModelType(String modelType) {
        this.modelType = modelType;
    }
} 