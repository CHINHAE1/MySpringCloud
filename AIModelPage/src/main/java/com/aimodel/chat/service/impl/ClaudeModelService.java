package com.aimodel.chat.service.impl;

import com.aimodel.chat.model.ChatResponse;
import com.aimodel.chat.service.ModelService;
import okhttp3.*;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;
import okio.BufferedSource;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

/**
 * Claude模型服务实现类
 */
@Service("claudeModelService")
public class ClaudeModelService implements ModelService {
    
    @Value("${ai.model.claude.url}")
    private String apiUrl;
    
    @Value("${ai.model.claude.api-key}")
    private String apiKey;
    
    @Value("${ai.model.claude.model-name}")
    private String modelName;
    
    private final OkHttpClient client = new OkHttpClient.Builder()
            .connectTimeout(30, TimeUnit.SECONDS)  // 连接超时时间
            .writeTimeout(30, TimeUnit.SECONDS)    // 写入超时时间
            .readTimeout(120, TimeUnit.SECONDS)    // 读取超时时间，对于AI生成内容需要更长时间
            .build();
    
    /**
     * 生成Claude模型响应
     *
     * @param message 用户输入的消息
     * @return 聊天响应
     */
    @Override
    public ChatResponse generateResponse(String message) {
        try {
            // 构建请求体
            JSONObject requestBody = new JSONObject();
            requestBody.put("model", modelName);
            
            // 构建消息
            JSONArray messages = new JSONArray();
            JSONObject userMessage = new JSONObject();
            userMessage.put("role", "user");
            userMessage.put("content", message);
            messages.put(userMessage);
            requestBody.put("messages", messages);
            
            // 设置最大token数
            requestBody.put("max_tokens", 2000);
            
            // 构建请求
            RequestBody body = RequestBody.create(
                    MediaType.parse("application/json"), requestBody.toString());
            
            Request request = new Request.Builder()
                    .url(apiUrl)
                    .post(body)
                    .addHeader("x-api-key", apiKey)
                    .addHeader("anthropic-version", "2023-06-01")
                    .addHeader("content-type", "application/json")
                    .build();
            
            // 发送请求
            try (Response response = client.newCall(request).execute()) {
                if (!response.isSuccessful()) {
                    return createErrorResponse("请求AI模型失败: " + response.code());
                }
                
                // 解析响应
                String responseBody = response.body().string();
                JSONObject jsonResponse = new JSONObject(responseBody);
                
                String content;
                int promptTokens = 0;
                int completionTokens = 0;
                
                try {
                    // 提取响应内容
                    JSONObject contentObj = jsonResponse.getJSONObject("content");
                    content = contentObj.getJSONArray("parts").getJSONObject(0).getString("text");
                    
                    // 获取token计数
                    if (jsonResponse.has("usage")) {
                        JSONObject usage = jsonResponse.getJSONObject("usage");
                        promptTokens = usage.optInt("input_tokens", 0);
                        completionTokens = usage.optInt("output_tokens", 0);
                    }
                } catch (Exception e) {
                    return createErrorResponse("解析AI响应时出错: " + e.getMessage());
                }
                
                // 创建响应对象
                return new ChatResponse(
                        content,
                        modelName,
                        "Anthropic",
                        promptTokens,
                        completionTokens
                );
            }
        } catch (Exception e) {
            return createErrorResponse("请求AI模型时发生异常: " + e.getMessage());
        }
    }
    
    /**
     * 创建错误响应
     *
     * @param errorMessage 错误信息
     * @return 聊天响应
     */
    private ChatResponse createErrorResponse(String errorMessage) {
        return new ChatResponse(
                errorMessage,
                modelName,
                "Error",
                0,
                0
        );
    }
    
    /**
     * 生成流式AI模型响应
     *
     * @param message 用户输入的消息
     * @param emitter 用于发送流式响应的SSE发射器
     */
    @Override
    public void generateStreamResponse(String message, SseEmitter emitter) {
        try {
            // 构建请求体
            JSONObject requestBody = new JSONObject();
            requestBody.put("model", modelName);
            
            // 构建消息
            JSONArray messages = new JSONArray();
            JSONObject userMessage = new JSONObject();
            userMessage.put("role", "user");
            userMessage.put("content", message);
            messages.put(userMessage);
            requestBody.put("messages", messages);
            
            // 设置最大token数和流式输出
            requestBody.put("max_tokens", 2000);
            requestBody.put("stream", true);
            
            // 构建请求
            RequestBody body = RequestBody.create(
                    MediaType.parse("application/json"), requestBody.toString());
            
            Request request = new Request.Builder()
                    .url(apiUrl)
                    .post(body)
                    .addHeader("x-api-key", apiKey)
                    .addHeader("anthropic-version", "2023-06-01")
                    .addHeader("content-type", "application/json")
                    .build();
            
            // 发送异步请求
            client.newCall(request).enqueue(new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    try {
                        emitter.send(SseEmitter.event()
                                .name("error")
                                .data("网络请求失败: " + e.getMessage()));
                        emitter.complete();
                    } catch (IOException ex) {
                        emitter.completeWithError(ex);
                    }
                }
                
                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    if (!response.isSuccessful()) {
                        try {
                            emitter.send(SseEmitter.event()
                                    .name("error")
                                    .data("请求失败: " + response.code()));
                            emitter.complete();
                            return;
                        } catch (IOException ex) {
                            emitter.completeWithError(ex);
                            return;
                        }
                    }
                    
                    try (ResponseBody responseBody = response.body()) {
                        if (responseBody == null) {
                            emitter.send(SseEmitter.event()
                                    .name("error")
                                    .data("服务器返回空响应"));
                            emitter.complete();
                            return;
                        }
                        
                        // 初始化计数器和内容
                        int[] tokenCount = {0};
                        StringBuilder fullContent = new StringBuilder();
                        
                        // 读取响应流
                        BufferedSource source = responseBody.source();
                        while (!source.exhausted()) {
                            String line = source.readUtf8Line();
                            if (line == null) break;
                            
                            // 处理Claude的SSE格式
                            if (line.startsWith("data: ")) {
                                String data = line.substring(6).trim();
                                
                                // 检查是否完成
                                if (data.equals("[DONE]")) {
                                    // 发送完成事件
                                    JSONObject completeData = new JSONObject();
                                    completeData.put("content", fullContent.toString());
                                    completeData.put("tokensIn", message.length() / 4); // 估算输入token数
                                    completeData.put("tokensOut", tokenCount[0]);
                                    
                                    emitter.send(SseEmitter.event()
                                            .name("complete")
                                            .data(completeData.toString()));
                                    // 收到[DONE]后直接完成并返回，避免后续重复发送
                                    emitter.complete();
                                    return;
                                }
                                
                                try {
                                    JSONObject jsonChunk = new JSONObject(data);
                                    
                                    // 处理Claude流式响应格式
                                    if (jsonChunk.has("type") && jsonChunk.getString("type").equals("content_block_delta")) {
                                        if (jsonChunk.has("delta") && jsonChunk.getJSONObject("delta").has("text")) {
                                            String content = jsonChunk.getJSONObject("delta").getString("text");
                                            fullContent.append(content);
                                            tokenCount[0]++;
                                            
                                            // 发送内容
                                            emitter.send(SseEmitter.event()
                                                    .name("content")
                                                    .data(content));
                                        }
                                    }
                                    
                                    // 检查是否有完成消息
                                    if (jsonChunk.has("type") && jsonChunk.getString("type").equals("message_stop")) {
                                        JSONObject completeData = new JSONObject();
                                        completeData.put("content", fullContent.toString());
                                        
                                        // 获取token使用量（如果有）
                                        if (jsonChunk.has("usage")) {
                                            JSONObject usage = jsonChunk.getJSONObject("usage");
                                            int inputTokens = usage.optInt("input_tokens", message.length() / 4);
                                            int outputTokens = usage.optInt("output_tokens", tokenCount[0]);
                                            
                                            completeData.put("tokensIn", inputTokens);
                                            completeData.put("tokensOut", outputTokens);
                                        } else {
                                            completeData.put("tokensIn", message.length() / 4);
                                            completeData.put("tokensOut", tokenCount[0]);
                                        }
                                        
                                        emitter.send(SseEmitter.event()
                                                .name("complete")
                                                .data(completeData.toString()));
                                        // 收到message_stop后直接完成并返回
                                        emitter.complete();
                                        return;
                                    }
                                } catch (JSONException e) {
                                    // 忽略解析错误，继续处理
                                }
                            }
                        }
                        
                        // 如果没有正常结束，发送自定义的完成事件
                        try {
                            if (fullContent.length() > 0) {
                                JSONObject completeData = new JSONObject();
                                completeData.put("content", fullContent.toString());
                                completeData.put("tokensIn", message.length() / 4);
                                completeData.put("tokensOut", tokenCount[0]);
                                
                                emitter.send(SseEmitter.event()
                                        .name("complete")
                                        .data(completeData.toString()));
                            }
                            
                            emitter.complete();
                        } catch (Exception ex) {
                            // 如果emitter已完成，忽略异常
                            emitter.completeWithError(ex);
                        }
                    } catch (Exception e) {
                        try {
                            emitter.send(SseEmitter.event()
                                    .name("error")
                                    .data("处理响应流时出错: " + e.getMessage()));
                            emitter.complete();
                        } catch (IOException ex) {
                            emitter.completeWithError(ex);
                        }
                    }
                }
            });
        } catch (Exception e) {
            try {
                emitter.send(SseEmitter.event()
                        .name("error")
                        .data("创建请求时出错: " + e.getMessage()));
                emitter.complete();
            } catch (IOException ex) {
                emitter.completeWithError(ex);
            }
        }
    }
} 