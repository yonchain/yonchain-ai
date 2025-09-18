/*
 * Copyright 2025-2028 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.yonchain.ai.plugin.exception;

import com.yonchain.ai.api.exception.YonchainException;

/**
 * 插件系统基础异常类
 * 所有插件相关的异常都应该继承此类
 * 
 * @author yonchain
 * @since 1.0.0
 */
public class PluginException extends YonchainException {
    
    /**
     * 插件ID（可选）
     */
    private String pluginId;
    
    /**
     * 错误代码（可选）
     */
    private String errorCode;
    
    /**
     * 构造函数
     * 
     * @param message 错误信息
     */
    public PluginException(String message) {
        super(message);
    }
    
    /**
     * 构造函数
     * 
     * @param message 错误信息
     * @param cause 原因异常
     */
    public PluginException(String message, Throwable cause) {
        super(message, cause);
    }
    
    /**
     * 构造函数
     * 
     * @param pluginId 插件ID
     * @param message 错误信息
     */
    public PluginException(String pluginId, String message) {
        super(message);
        this.pluginId = pluginId;
    }
    
    /**
     * 构造函数
     * 
     * @param pluginId 插件ID
     * @param message 错误信息
     * @param cause 原因异常
     */
    public PluginException(String pluginId, String message, Throwable cause) {
        super(message, cause);
        this.pluginId = pluginId;
    }
    
    /**
     * 构造函数
     * 
     * @param pluginId 插件ID
     * @param errorCode 错误代码
     * @param message 错误信息
     */
    public PluginException(String pluginId, String errorCode, String message) {
        super(message);
        this.pluginId = pluginId;
        this.errorCode = errorCode;
    }
    
    /**
     * 构造函数
     * 
     * @param pluginId 插件ID
     * @param errorCode 错误代码
     * @param message 错误信息
     * @param cause 原因异常
     */
    public PluginException(String pluginId, String errorCode, String message, Throwable cause) {
        super(message, cause);
        this.pluginId = pluginId;
        this.errorCode = errorCode;
    }
    
    /**
     * 获取插件ID
     * 
     * @return 插件ID
     */
    public String getPluginId() {
        return pluginId;
    }
    
    /**
     * 设置插件ID
     * 
     * @param pluginId 插件ID
     */
    public void setPluginId(String pluginId) {
        this.pluginId = pluginId;
    }
    
    /**
     * 获取错误代码
     * 
     * @return 错误代码
     */
    public String getErrorCode() {
        return errorCode;
    }
    
    /**
     * 设置错误代码
     * 
     * @param errorCode 错误代码
     */
    public void setErrorCode(String errorCode) {
        this.errorCode = errorCode;
    }
    
    /**
     * 获取详细错误信息
     * 
     * @return 详细错误信息
     */
    public String getDetailedMessage() {
        StringBuilder sb = new StringBuilder();
        
        if (pluginId != null && !pluginId.trim().isEmpty()) {
            sb.append("Plugin[").append(pluginId).append("] ");
        }
        
        if (errorCode != null && !errorCode.trim().isEmpty()) {
            sb.append("Error[").append(errorCode).append("] ");
        }
        
        sb.append(getMessage());
        
        return sb.toString();
    }
    
    @Override
    public String toString() {
        return "PluginException{" +
                "pluginId='" + pluginId + '\'' +
                ", errorCode='" + errorCode + '\'' +
                ", message='" + getMessage() + '\'' +
                '}';
    }
}