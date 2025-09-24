package com.yonchain.ai.model.request;

import org.springframework.ai.chat.messages.Message;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.chat.prompt.ChatOptions;

import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;

/**
 * 聊天请求类
 */
public class ChatRequest {
    
    private List<Message> messages;
    private ChatOptions options;
    
    // 原始参数，用于延迟处理
    private Map<String, Object> rawParameters;
    
    public ChatRequest() {
        this.messages = new ArrayList<>();
    }
    
    public ChatRequest(List<Message> messages) {
        this.messages = messages != null ? messages : new ArrayList<>();
    }
    
    public ChatRequest(String message) {
        this();
        this.messages.add(new UserMessage(message));
    }
    
    public List<Message> getMessages() {
        return messages;
    }
    
    public void setMessages(List<Message> messages) {
        this.messages = messages;
    }
    
    public ChatOptions getOptions() {
        return options;
    }
    
    public void setOptions(ChatOptions options) {
        this.options = options;
    }
    
    public Map<String, Object> getRawParameters() {
        return rawParameters;
    }
    
    public void setRawParameters(Map<String, Object> rawParameters) {
        this.rawParameters = rawParameters;
    }
    
    /**
     * 添加消息
     * 
     * @param message 消息
     * @return 当前请求对象
     */
    public ChatRequest addMessage(Message message) {
        this.messages.add(message);
        return this;
    }
    
    /**
     * 添加用户消息
     * 
     * @param content 消息内容
     * @return 当前请求对象
     */
    public ChatRequest addUserMessage(String content) {
        this.messages.add(new UserMessage(content));
        return this;
    }
    
    /**
     * 转换为Spring AI的Prompt
     * 
     * @return Prompt对象
     */
    public Prompt toPrompt() {
        if (options != null) {
            return new Prompt(messages, options);
        } else {
            return new Prompt(messages);
        }
    }
    
    /**
     * 转换为Spring AI的Prompt（使用运行时选项）
     * 
     * @param runtimeOptions 运行时模型选项
     * @return Prompt对象
     */
    public Prompt toPrompt(ChatOptions runtimeOptions) {
        if (runtimeOptions != null) {
            return new Prompt(messages, runtimeOptions);
        } else {
            return toPrompt();
        }
    }
    
    /**
     * 构建器
     * 
     * @return ChatRequest构建器
     */
    public static Builder builder() {
        return new Builder();
    }
    
    public static class Builder {
        private final ChatRequest request = new ChatRequest();
        
        public Builder message(String content) {
            request.addUserMessage(content);
            return this;
        }
        
        public Builder message(Message message) {
            request.addMessage(message);
            return this;
        }
        
        public Builder messages(List<Message> messages) {
            request.setMessages(messages);
            return this;
        }
        
        public Builder options(ChatOptions options) {
            request.setOptions(options);
            return this;
        }
        
        public Builder rawParameters(Map<String, Object> rawParameters) {
            request.setRawParameters(rawParameters);
            return this;
        }
        
        public ChatRequest build() {
            return request;
        }
    }
}
