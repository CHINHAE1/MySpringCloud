package com.aimodel.chat.service;

import com.aimodel.chat.model.ChatResponse;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

/**
 * AI模型服务接口
 */
public interface ModelService {
    
    /**
     * 生成AI模型响应
     *
     * @param message 用户输入的消息
     * @return 聊天响应
     */
    ChatResponse generateResponse(String message);
    
    /**
     * 生成流式AI模型响应
     *
     * @param message 用户输入的消息
     * @param emitter 用于发送流式响应的SSE发射器
     */
    void generateStreamResponse(String message, SseEmitter emitter);
} 