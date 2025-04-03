package com.aimodel.chat.service;

import com.aimodel.chat.model.ChatResponse;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

/**
 * AI聊天服务接口
 */
public interface AIChatService {
    
    /**
     * 处理聊天消息
     *
     * @param message 用户输入的消息
     * @param modelType 使用的AI模型类型
     * @return 聊天响应
     */
    ChatResponse processChat(String message, String modelType);
    
    /**
     * 处理流式聊天消息
     *
     * @param message 用户输入的消息
     * @param modelType 使用的AI模型类型
     * @param emitter 用于发送流式响应的SSE发射器
     */
    void processStreamChat(String message, String modelType, SseEmitter emitter);
} 