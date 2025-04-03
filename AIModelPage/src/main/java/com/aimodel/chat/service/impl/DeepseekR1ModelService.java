package com.aimodel.chat.service.impl;

import com.aimodel.chat.model.ChatResponse;
import com.aimodel.chat.service.ModelService;
import okhttp3.*;
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
 * Deepseek-R1模型服务实现类
 */
@Service("deepseekR1ModelService")
public class DeepseekR1ModelService implements ModelService {
    
    @Value("${ai.model.deepseek.r1.url}")
    private String apiUrl;
    
    @Value("${ai.model.deepseek.r1.model-name}")
    private String modelName;
    
    @Value("${ai.model.deepseek.r1.api-key}")
    private String apiKey;
    
    // 增加超时设置
    private final OkHttpClient client = new OkHttpClient.Builder()
            .connectTimeout(30, TimeUnit.SECONDS)  // 连接超时时间
            .writeTimeout(30, TimeUnit.SECONDS)    // 写入超时时间
            .readTimeout(120, TimeUnit.SECONDS)    // 读取超时时间，对于AI生成内容需要更长时间
            .build();
    
    /**
     * 生成Deepseek-R1模型响应
     *
     * @param message 用户输入的消息
     * @return 聊天响应
     */
    @Override
    public ChatResponse generateResponse(String message) {
        try {
            // 构建官方API请求体
            JSONObject requestBody = new JSONObject();
            requestBody.put("model", modelName);
            
            // 构建消息数组
            JSONArray messages = new JSONArray();
            JSONObject userMessage = new JSONObject();
            userMessage.put("role", "user");
            userMessage.put("content", message);
            messages.put(userMessage);
            requestBody.put("messages", messages);
            
            // 其他参数
            requestBody.put("temperature", 0.7);
            requestBody.put("max_tokens", 2000);
            
            // 构建请求
            RequestBody body = RequestBody.create(
                    MediaType.parse("application/json"), requestBody.toString());
            
            Request request = new Request.Builder()
                    .url(apiUrl)
                    .post(body)
                    .addHeader("Authorization", "Bearer " + apiKey)
                    .addHeader("Content-Type", "application/json")
                    .build();
            
            // 发送请求
            try (Response response = client.newCall(request).execute()) {
                if (!response.isSuccessful()) {
                    return createErrorResponse(handleErrorCode(response.code()));
                }
                
                // 解析响应 (适配官方API响应格式)
                String responseBody = response.body().string();
                JSONObject jsonResponse = new JSONObject(responseBody);
                
                String content;
                int promptTokens = 0;
                int completionTokens = 0;
                
                try {
                    // 从choices中提取响应内容
                    content = jsonResponse.getJSONArray("choices")
                                .getJSONObject(0)
                                .getJSONObject("message")
                                .getString("content");
                    
                    // 获取token计数
                    if (jsonResponse.has("usage")) {
                        JSONObject usage = jsonResponse.getJSONObject("usage");
                        promptTokens = usage.optInt("prompt_tokens", 0);
                        completionTokens = usage.optInt("completion_tokens", 0);
                    }
                } catch (Exception e) {
                    return createErrorResponse("解析AI响应时出错: " + e.getMessage());
                }
                
                return new ChatResponse(
                        content,
                        modelName,
                        "Deepseek",
                        promptTokens,
                        completionTokens
                );
            }
        } catch (Exception e) {
            return createErrorResponse("请求AI模型时发生异常: " + e.getMessage());
        }
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
            // 构建官方API请求体
            JSONObject requestBody = new JSONObject();
            requestBody.put("model", modelName);
            
            // 构建消息数组
            JSONArray messages = new JSONArray();
            JSONObject userMessage = new JSONObject();
            userMessage.put("role", "user");
            userMessage.put("content", message);
            messages.put(userMessage);
            requestBody.put("messages", messages);
            
            // 设置流式输出
            requestBody.put("stream", true);
            requestBody.put("temperature", 0.7);
            requestBody.put("max_tokens", 2000);
            
            // 构建请求
            RequestBody body = RequestBody.create(
                    MediaType.parse("application/json"), requestBody.toString());
            
            Request request = new Request.Builder()
                    .url(apiUrl)
                    .post(body)
                    .addHeader("Authorization", "Bearer " + apiKey)
                    .addHeader("Content-Type", "application/json")
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
                            String errorMessage = handleErrorCode(response.code());
                            emitter.send(SseEmitter.event()
                                    .name("error")
                                    .data(errorMessage));
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
                        
                        // 初始化计数器
                        int[] tokenCount = {0};
                        StringBuilder fullContent = new StringBuilder();
                        
                        // 读取响应流
                        BufferedSource source = responseBody.source();
                        while (!source.exhausted()) {
                            String line = source.readUtf8Line();
                            if (line == null) break;
                            
                            // 处理SSE格式数据
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
                                    
                                    // 提取内容，处理选择
                                    if (jsonChunk.has("choices")) {
                                        JSONArray choices = jsonChunk.getJSONArray("choices");
                                        if (choices.length() > 0) {
                                            JSONObject choice = choices.getJSONObject(0);
                                            
                                            // 处理delta
                                            if (choice.has("delta")) {
                                                JSONObject delta = choice.getJSONObject("delta");
                                                if (delta.has("content")) {
                                                    String content = delta.getString("content");
                                                    fullContent.append(content);
                                                    tokenCount[0]++;
                                                    
                                                    // 发送内容
                                                    emitter.send(SseEmitter.event()
                                                            .name("content")
                                                            .data(content));
                                                }
                                            }
                                        }
                                    }
                                } catch (JSONException e) {
                                    // 忽略解析错误，继续处理
                                }
                            }
                        }
                        
                        // 如果没有收到[DONE]标记，则在这里发送完成事件
                        // 使用标志位避免重复发送完成事件
                        JSONObject completeData = new JSONObject();
                        completeData.put("content", fullContent.toString());
                        completeData.put("tokensIn", message.length() / 4); // 估算输入token数
                        completeData.put("tokensOut", tokenCount[0]);
                        
                        try {
                            emitter.send(SseEmitter.event()
                                    .name("complete")
                                    .data(completeData.toString()));
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
    
    /**
     * 处理HTTP错误码
     * 
     * @param code HTTP错误码
     * @return 处理后的错误消息
     */
    private String handleErrorCode(int code) {
        String errorDescription;
        String[] solutions;
        
        switch (code) {
            case 400:
                errorDescription = "请求格式错误";
                solutions = new String[] {
                    "检查请求体格式是否正确",
                    "确保所有必要参数已提供",
                    "检查API版本是否兼容"
                };
                break;
            case 401:
                errorDescription = "未授权访问";
                solutions = new String[] {
                    "检查API密钥是否正确",
                    "确认API密钥未过期",
                    "检查账户余额是否充足"
                };
                break;
            case 404:
                errorDescription = "资源未找到";
                solutions = new String[] {
                    "检查API端点URL是否正确",
                    "确认模型名称是否正确",
                    "验证模型是否可用"
                };
                break;
            case 429:
                errorDescription = "请求过于频繁";
                solutions = new String[] {
                    "减少API调用频率",
                    "实现请求重试机制",
                    "考虑升级账户套餐"
                };
                break;
            case 500:
                errorDescription = "服务器内部错误";
                solutions = new String[] {
                    "稍后重试",
                    "联系DeepSeek技术支持",
                    "检查请求是否触发了服务器异常"
                };
                break;
            case 503:
                errorDescription = "服务不可用";
                solutions = new String[] {
                    "检查DeepSeek服务状态",
                    "实现指数退避重试机制",
                    "稍后再试"
                };
                break;
            default:
                errorDescription = "未知错误";
                solutions = new String[] {
                    "检查网络连接",
                    "联系技术支持",
                    "尝试使用其他模型"
                };
        }
        
        // 构建错误消息
        StringBuilder errorMessage = new StringBuilder();
        errorMessage.append("## 错误: ").append(code).append(" - ").append(errorDescription).append("\n\n");
        errorMessage.append("### 可能的解决方案:\n");
        for (String solution : solutions) {
            errorMessage.append("- ").append(solution).append("\n");
        }
        
        return errorMessage.toString();
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
} 