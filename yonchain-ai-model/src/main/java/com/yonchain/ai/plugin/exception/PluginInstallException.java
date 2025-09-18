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

/**
 * 插件安装异常类
 * 用于插件安装过程中出现的各种错误
 * 
 * @author yonchain
 * @since 1.0.0
 */
public class PluginInstallException extends PluginException {
    
    /**
     * 安装阶段
     */
    public enum InstallStage {
        /**
         * 下载阶段
         */
        DOWNLOAD("download"),
        
        /**
         * 验证阶段
         */
        VALIDATION("validation"),
        
        /**
         * 解析阶段
         */
        PARSING("parsing"),
        
        /**
         * 依赖检查阶段
         */
        DEPENDENCY_CHECK("dependency_check"),
        
        /**
         * 文件复制阶段
         */
        FILE_COPY("file_copy"),
        
        /**
         * 注册阶段
         */
        REGISTRATION("registration"),
        
        /**
         * 初始化阶段
         */
        INITIALIZATION("initialization"),
        
        /**
         * 清理阶段
         */
        CLEANUP("cleanup");
        
        private final String code;
        
        InstallStage(String code) {
            this.code = code;
        }
        
        public String getCode() {
            return code;
        }
        
        @Override
        public String toString() {
            return code;
        }
    }
    
    /**
     * 安装阶段
     */
    private InstallStage installStage;
    
    /**
     * 插件文件路径
     */
    private String pluginFilePath;
    
    /**
     * 构造函数
     * 
     * @param message 错误信息
     */
    public PluginInstallException(String message) {
        super(message);
    }
    
    /**
     * 构造函数
     * 
     * @param message 错误信息
     * @param cause 原因异常
     */
    public PluginInstallException(String message, Throwable cause) {
        super(message, cause);
    }
    
    /**
     * 构造函数
     * 
     * @param pluginId 插件ID
     * @param message 错误信息
     */
    public PluginInstallException(String pluginId, String message) {
        super(pluginId, message);
    }
    
    /**
     * 构造函数
     * 
     * @param pluginId 插件ID
     * @param message 错误信息
     * @param cause 原因异常
     */
    public PluginInstallException(String pluginId, String message, Throwable cause) {
        super(pluginId, message, cause);
    }
    
    /**
     * 构造函数
     * 
     * @param pluginId 插件ID
     * @param installStage 安装阶段
     * @param message 错误信息
     */
    public PluginInstallException(String pluginId, InstallStage installStage, String message) {
        super(pluginId, installStage.getCode(), message);
        this.installStage = installStage;
    }
    
    /**
     * 构造函数
     * 
     * @param pluginId 插件ID
     * @param installStage 安装阶段
     * @param message 错误信息
     * @param cause 原因异常
     */
    public PluginInstallException(String pluginId, InstallStage installStage, String message, Throwable cause) {
        super(pluginId, installStage.getCode(), message, cause);
        this.installStage = installStage;
    }
    
    /**
     * 构造函数
     * 
     * @param pluginId 插件ID
     * @param installStage 安装阶段
     * @param pluginFilePath 插件文件路径
     * @param message 错误信息
     */
    public PluginInstallException(String pluginId, InstallStage installStage, String pluginFilePath, String message) {
        super(pluginId, installStage.getCode(), message);
        this.installStage = installStage;
        this.pluginFilePath = pluginFilePath;
    }
    
    /**
     * 构造函数
     * 
     * @param pluginId 插件ID
     * @param installStage 安装阶段
     * @param pluginFilePath 插件文件路径
     * @param message 错误信息
     * @param cause 原因异常
     */
    public PluginInstallException(String pluginId, InstallStage installStage, String pluginFilePath, String message, Throwable cause) {
        super(pluginId, installStage.getCode(), message, cause);
        this.installStage = installStage;
        this.pluginFilePath = pluginFilePath;
    }
    
    /**
     * 获取安装阶段
     * 
     * @return 安装阶段
     */
    public InstallStage getInstallStage() {
        return installStage;
    }
    
    /**
     * 设置安装阶段
     * 
     * @param installStage 安装阶段
     */
    public void setInstallStage(InstallStage installStage) {
        this.installStage = installStage;
        this.setErrorCode(installStage != null ? installStage.getCode() : null);
    }
    
    /**
     * 获取插件文件路径
     * 
     * @return 插件文件路径
     */
    public String getPluginFilePath() {
        return pluginFilePath;
    }
    
    /**
     * 设置插件文件路径
     * 
     * @param pluginFilePath 插件文件路径
     */
    public void setPluginFilePath(String pluginFilePath) {
        this.pluginFilePath = pluginFilePath;
    }
    
    /**
     * 获取详细错误信息
     * 
     * @return 详细错误信息
     */
    @Override
    public String getDetailedMessage() {
        StringBuilder sb = new StringBuilder();
        
        if (getPluginId() != null && !getPluginId().trim().isEmpty()) {
            sb.append("Plugin[").append(getPluginId()).append("] ");
        }
        
        if (installStage != null) {
            sb.append("Stage[").append(installStage.getCode()).append("] ");
        }
        
        if (pluginFilePath != null && !pluginFilePath.trim().isEmpty()) {
            sb.append("File[").append(pluginFilePath).append("] ");
        }
        
        sb.append(getMessage());
        
        return sb.toString();
    }
    
    /**
     * 创建下载阶段异常
     * 
     * @param pluginId 插件ID
     * @param message 错误信息
     * @return 安装异常
     */
    public static PluginInstallException downloadError(String pluginId, String message) {
        return new PluginInstallException(pluginId, InstallStage.DOWNLOAD, message);
    }
    
    /**
     * 创建下载阶段异常
     * 
     * @param pluginId 插件ID
     * @param message 错误信息
     * @param cause 原因异常
     * @return 安装异常
     */
    public static PluginInstallException downloadError(String pluginId, String message, Throwable cause) {
        return new PluginInstallException(pluginId, InstallStage.DOWNLOAD, message, cause);
    }
    
    /**
     * 创建验证阶段异常
     * 
     * @param pluginId 插件ID
     * @param message 错误信息
     * @return 安装异常
     */
    public static PluginInstallException validationError(String pluginId, String message) {
        return new PluginInstallException(pluginId, InstallStage.VALIDATION, message);
    }
    
    /**
     * 创建解析阶段异常
     * 
     * @param pluginId 插件ID
     * @param message 错误信息
     * @return 安装异常
     */
    public static PluginInstallException parseError(String pluginId, String message) {
        return new PluginInstallException(pluginId, InstallStage.PARSING, message);
    }
    
    /**
     * 创建依赖检查阶段异常
     * 
     * @param pluginId 插件ID
     * @param message 错误信息
     * @return 安装异常
     */
    public static PluginInstallException dependencyError(String pluginId, String message) {
        return new PluginInstallException(pluginId, InstallStage.DEPENDENCY_CHECK, message);
    }
    
    @Override
    public String toString() {
        return "PluginInstallException{" +
                "pluginId='" + getPluginId() + '\'' +
                ", installStage=" + installStage +
                ", pluginFilePath='" + pluginFilePath + '\'' +
                ", message='" + getMessage() + '\'' +
                '}';
    }
}