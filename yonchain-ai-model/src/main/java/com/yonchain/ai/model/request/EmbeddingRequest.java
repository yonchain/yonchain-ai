package com.yonchain.ai.model.request;

import org.springframework.ai.embedding.EmbeddingOptions;

import java.util.List;
import java.util.Arrays;

/**
 * 嵌入请求类
 */
public class EmbeddingRequest {
    
    private List<String> texts;
    private EmbeddingOptions options;
    
    public EmbeddingRequest() {
    }
    
    public EmbeddingRequest(String text) {
        this.texts = Arrays.asList(text);
    }
    
    public EmbeddingRequest(List<String> texts) {
        this.texts = texts;
    }
    
    public List<String> getTexts() {
        return texts;
    }
    
    public void setTexts(List<String> texts) {
        this.texts = texts;
    }
    
    public String getText() {
        return texts != null && !texts.isEmpty() ? texts.get(0) : null;
    }
    
    public void setText(String text) {
        this.texts = Arrays.asList(text);
    }
    
    public EmbeddingOptions getOptions() {
        return options;
    }
    
    public void setOptions(EmbeddingOptions options) {
        this.options = options;
    }
    
    /**
     * 转换为Spring AI的EmbeddingRequest
     * 
     * @return Spring AI EmbeddingRequest对象
     */
    public org.springframework.ai.embedding.EmbeddingRequest toEmbeddingRequest() {
        if (options != null) {
            return new org.springframework.ai.embedding.EmbeddingRequest(texts, options);
        } else {
            return new org.springframework.ai.embedding.EmbeddingRequest(texts, null);
        }
    }
    
    /**
     * 构建器
     * 
     * @return EmbeddingRequest构建器
     */
    public static Builder builder() {
        return new Builder();
    }
    
    public static class Builder {
        private final EmbeddingRequest request = new EmbeddingRequest();
        
        public Builder text(String text) {
            request.setText(text);
            return this;
        }
        
        public Builder texts(List<String> texts) {
            request.setTexts(texts);
            return this;
        }
        
        public Builder options(EmbeddingOptions options) {
            request.setOptions(options);
            return this;
        }
        
        public EmbeddingRequest build() {
            return request;
        }
    }
}
