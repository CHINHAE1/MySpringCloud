package com.aimodel.chat.controller;

import com.aimodel.chat.model.ChatMessage;
import com.aimodel.chat.model.ChatRequest;
import com.aimodel.chat.model.ChatResponse;
import com.aimodel.chat.service.AIChatService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.concurrent.CompletableFuture;

/**
 * 聊天控制器，处理聊天相关的请求
 */
@Controller
public class ChatController {

    @Autowired
    private AIChatService aiChatService;

    /**
     * 主页面
     */
    @GetMapping("/")
    public String index() {
        return "chat";
    }

    /**
     * 处理聊天API请求
     */
    @PostMapping("/api/chat")
    @ResponseBody
    public ResponseEntity<ChatResponse> chat(@RequestBody ChatRequest request) {
        ChatResponse response = aiChatService.processChat(request.getMessage(), request.getModelType());
        return ResponseEntity.ok(response);
    }
    
    /**
     * 处理流式聊天API请求
     */
    @GetMapping(value = "/api/chat/stream", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    @ResponseBody
    public SseEmitter chatStream(@RequestParam("message") String message, @RequestParam("modelType") String modelType) {
        SseEmitter emitter = new SseEmitter(-1L); // 无超时限制
        
        // 异步处理流式响应
        CompletableFuture.runAsync(() -> {
            try {
                // 处理流式聊天响应
                aiChatService.processStreamChat(message, modelType, emitter);
            } catch (Exception e) {
                try {
                    // 发送错误消息
                    emitter.send(SseEmitter.event()
                            .name("error")
                            .data("发生错误: " + e.getMessage()));
                    emitter.complete();
                } catch (IOException ex) {
                    emitter.completeWithError(ex);
                }
            }
        });
        
        return emitter;
    }
} 