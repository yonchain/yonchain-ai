package com.yonchain.ai.model.request;

import org.springframework.ai.image.ImagePrompt;
import org.springframework.ai.image.ImageOptions;

/**
 * 图像生成请求类
 */
public class ImageRequest {
    
    private String prompt;
    private ImageOptions options;
    
    public ImageRequest() {
    }
    
    public ImageRequest(String prompt) {
        this.prompt = prompt;
    }
    
    public String getPrompt() {
        return prompt;
    }
    
    public void setPrompt(String prompt) {
        this.prompt = prompt;
    }
    
    public ImageOptions getOptions() {
        return options;
    }
    
    public void setOptions(ImageOptions options) {
        this.options = options;
    }
    
    /**
     * 转换为Spring AI的ImagePrompt
     * 
     * @return ImagePrompt对象
     */
    public ImagePrompt toImagePrompt() {
        if (options != null) {
            return new ImagePrompt(prompt, options);
        } else {
            return new ImagePrompt(prompt);
        }
    }
    
    /**
     * 构建器
     * 
     * @return ImageRequest构建器
     */
    public static Builder builder() {
        return new Builder();
    }
    
    public static class Builder {
        private final ImageRequest request = new ImageRequest();
        
        public Builder prompt(String prompt) {
            request.setPrompt(prompt);
            return this;
        }
        
        public Builder options(ImageOptions options) {
            request.setOptions(options);
            return this;
        }
        
        public Builder size(String size) {
            // 这里可以根据需要设置ImageOptions的size属性
            // 具体实现取决于Spring AI的ImageOptions API
            return this;
        }
        
        public Builder quality(String quality) {
            // 这里可以根据需要设置ImageOptions的quality属性
            return this;
        }
        
        public ImageRequest build() {
            return request;
        }
    }
}

