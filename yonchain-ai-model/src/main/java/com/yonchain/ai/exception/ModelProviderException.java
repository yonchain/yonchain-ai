package com.yonchain.ai.exception;

/**
 * 模型提供商异常
 */
public class ModelProviderException extends RuntimeException {
    
    public ModelProviderException(String message) {
        super(message);
    }
    
    public ModelProviderException(String message, Throwable cause) {
        super(message, cause);
    }
}
