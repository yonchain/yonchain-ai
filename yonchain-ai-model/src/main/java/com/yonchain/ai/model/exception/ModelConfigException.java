package com.yonchain.ai.model.exception;

/**
 * 模型配置异常
 */
public class ModelConfigException extends RuntimeException {
    
    public ModelConfigException(String message) {
        super(message);
    }
    
    public ModelConfigException(String message, Throwable cause) {
        super(message, cause);
    }
}
