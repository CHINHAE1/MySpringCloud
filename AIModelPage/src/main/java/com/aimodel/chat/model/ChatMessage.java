package com.aimodel.chat.model;

import java.time.LocalDateTime;

/**
 * 聊天消息模型类
 */
public class ChatMessage {
    
    private Long id;
    private String content;
    private String userId;
    private String modelType;
    private boolean isUserMessage;
    private LocalDateTime createTime;
    
    public ChatMessage() {
        this.createTime = LocalDateTime.now();
    }
    
    public ChatMessage(String content, String userId, String modelType, boolean isUserMessage) {
        this();
        this.content = content;
        this.userId = userId;
        this.modelType = modelType;
        this.isUserMessage = isUserMessage;
    }
    
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public String getContent() {
        return content;
    }
    
    public void setContent(String content) {
        this.content = content;
    }
    
    public String getUserId() {
        return userId;
    }
    
    public void setUserId(String userId) {
        this.userId = userId;
    }
    
    public String getModelType() {
        return modelType;
    }
    
    public void setModelType(String modelType) {
        this.modelType = modelType;
    }
    
    public boolean isUserMessage() {
        return isUserMessage;
    }
    
    public void setUserMessage(boolean userMessage) {
        isUserMessage = userMessage;
    }
    
    public LocalDateTime getCreateTime() {
        return createTime;
    }
    
    public void setCreateTime(LocalDateTime createTime) {
        this.createTime = createTime;
    }
} 