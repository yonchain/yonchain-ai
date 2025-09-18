package com.yonchain.ai.exception;

/**
 * 模型未找到异常
 */
public class ModelNotFoundException extends RuntimeException {
    
    public ModelNotFoundException(String message) {
        super(message);
    }
    
    public ModelNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}
