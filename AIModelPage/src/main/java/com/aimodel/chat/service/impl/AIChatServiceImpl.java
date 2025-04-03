package com.aimodel.chat.service.impl;

import com.aimodel.chat.model.ChatResponse;
import com.aimodel.chat.service.AIChatService;
import com.aimodel.chat.service.ModelService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.Map;

/**
 * AI聊天服务实现类
 */
@Service
public class AIChatServiceImpl implements AIChatService {
    
    private final Map<String, ModelService> modelServices;
    
    @Autowired
    public AIChatServiceImpl(Map<String, ModelService> modelServices) {
        this.modelServices = modelServices;
    }
    
    /**
     * 处理聊天消息
     *
     * @param message 用户输入的消息
     * @param modelType 使用的AI模型类型
     * @return 聊天响应
     */
    @Override
    public ChatResponse processChat(String message, String modelType) {
        // 根据模型类型选择相应的服务
        ModelService modelService = getModelService(modelType);
        
        // 调用AI模型服务处理消息
        return modelService.generateResponse(message);
    }
    
    /**
     * 处理流式聊天消息
     *
     * @param message 用户输入的消息
     * @param modelType 使用的AI模型类型
     * @param emitter 用于发送流式响应的SSE发射器
     */
    @Override
    public void processStreamChat(String message, String modelType, SseEmitter emitter) {
        // 根据模型类型选择相应的服务
        ModelService modelService = getModelService(modelType);
        
        try {
            // 调用AI模型服务处理流式消息
            modelService.generateStreamResponse(message, emitter);
        } catch (Exception e) {
            try {
                // 发送错误消息
                emitter.send(SseEmitter.event()
                        .name("error")
                        .data("模型服务错误: " + e.getMessage()));
                emitter.complete();
            } catch (IOException ex) {
                emitter.completeWithError(ex);
            }
        }
    }
    
    /**
     * 根据模型类型获取对应的模型服务
     *
     * @param modelType 模型类型
     * @return 模型服务
     */
    private ModelService getModelService(String modelType) {
        // 默认使用Deepseek R1模型
        String serviceKey = "deepseekR1ModelService";
        
        // 根据模型类型选择服务
        if (modelType != null) {
            switch (modelType.toLowerCase()) {
                case "deepseek-r1":
                    serviceKey = "deepseekR1ModelService";
                    break;
                case "deepseek-v3":
                    serviceKey = "deepseekV3ModelService";
                    break;
                case "claude-3-haiku":
                case "claude-3-opus":
                case "claude-3-sonnet":
                case "claude-3.5-sonnet":
                case "claude":
                    serviceKey = "claudeModelService";
                    break;
                default:
                    // 默认使用Deepseek R1
                    break;
            }
        }
        
        // 获取对应的服务
        ModelService service = modelServices.get(serviceKey);
        if (service == null) {
            // 如果没有找到对应的服务，使用默认的
            service = modelServices.get("deepseekR1ModelService");
        }
        
        return service;
    }
} 