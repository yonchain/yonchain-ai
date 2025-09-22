package com.yonchain.ai.plugin.exception;

/**
 * 插件解析异常
 * 
 * @author yonchain
 */
public class PluginParseException extends Exception {
    
    private final String pluginId;
    private final String errorCode;
    
    public PluginParseException(String message) {
        super(message);
        this.pluginId = null;
        this.errorCode = null;
    }
    
    public PluginParseException(String message, Throwable cause) {
        super(message, cause);
        this.pluginId = null;
        this.errorCode = null;
    }
    
    public PluginParseException(String message, String pluginId) {
        super(message);
        this.pluginId = pluginId;
        this.errorCode = null;
    }
    
    public PluginParseException(String message, String pluginId, String errorCode) {
        super(message);
        this.pluginId = pluginId;
        this.errorCode = errorCode;
    }
    
    public PluginParseException(String message, String pluginId, Throwable cause) {
        super(message, cause);
        this.pluginId = pluginId;
        this.errorCode = null;
    }
    
    public PluginParseException(String message, String pluginId, String errorCode, Throwable cause) {
        super(message, cause);
        this.pluginId = pluginId;
        this.errorCode = errorCode;
    }
    
    public String getPluginId() {
        return pluginId;
    }
    
    public String getErrorCode() {
        return errorCode;
    }
    
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder(super.toString());
        if (pluginId != null) {
            sb.append(" [Plugin: ").append(pluginId).append("]");
        }
        if (errorCode != null) {
            sb.append(" [Error Code: ").append(errorCode).append("]");
        }
        return sb.toString();
    }
}

