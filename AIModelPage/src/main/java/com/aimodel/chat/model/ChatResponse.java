package com.aimodel.chat.model;

/**
 * 聊天响应模型类
 */
public class ChatResponse {
    private String content;
    private String model;
    private String provider;
    private int tokensIn;
    private int tokensOut;
    private String reasoningContent;  // 新增思维链字段

    public ChatResponse(String content, String model, String provider, int tokensIn, int tokensOut) {
        this.content = content;
        this.model = model;
        this.provider = provider;
        this.tokensIn = tokensIn;
        this.tokensOut = tokensOut;
        this.reasoningContent = "";  // 默认为空字符串
    }

    // Getters and Setters
    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public String getProvider() {
        return provider;
    }

    public void setProvider(String provider) {
        this.provider = provider;
    }

    public int getTokensIn() {
        return tokensIn;
    }

    public void setTokensIn(int tokensIn) {
        this.tokensIn = tokensIn;
    }

    public int getTokensOut() {
        return tokensOut;
    }

    public void setTokensOut(int tokensOut) {
        this.tokensOut = tokensOut;
    }

    public String getReasoningContent() {
        return reasoningContent;
    }

    public void setReasoningContent(String reasoningContent) {
        this.reasoningContent = reasoningContent;
    }
}