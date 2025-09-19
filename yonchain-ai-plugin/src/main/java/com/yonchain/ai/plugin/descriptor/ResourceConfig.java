package com.yonchain.ai.plugin.descriptor;


/**
 * 插件资源配置
 * 对应plugin.yaml中的resource字段
 * 
 * @author yonchain
 */
public class ResourceConfig {
    
    /**
     * 内存配置（字节）
     */
    private Long memory;
    
    /**
     * 权限配置
     */
    private PermissionConfig permission;
    
    public ResourceConfig() {
    }
    
    public Long getMemory() {
        return memory;
    }
    
    public void setMemory(Long memory) {
        this.memory = memory;
    }
    
    public PermissionConfig getPermission() {
        return permission;
    }
    
    public void setPermission(PermissionConfig permission) {
        this.permission = permission;
    }
    
    /**
     * 权限配置
     */
    public static class PermissionConfig {
        
        /**
         * 模型权限
         */
        private ModelPermission model;
        
        /**
         * 工具权限
         */
        private ToolPermission tool;
        
        public ModelPermission getModel() {
            return model;
        }
        
        public void setModel(ModelPermission model) {
            this.model = model;
        }
        
        public ToolPermission getTool() {
            return tool;
        }
        
        public void setTool(ToolPermission tool) {
            this.tool = tool;
        }
    }
    
    /**
     * 模型权限配置
     */
    public static class ModelPermission {
        private Boolean enabled;
        private Boolean llm;
        private Boolean moderation;
        private Boolean rerank;
        private Boolean speech2text;
        private Boolean textEmbedding;
        private Boolean tts;
        
        // Getters and Setters
        public Boolean getEnabled() {
            return enabled;
        }
        
        public void setEnabled(Boolean enabled) {
            this.enabled = enabled;
        }
        
        public Boolean getLlm() {
            return llm;
        }
        
        public void setLlm(Boolean llm) {
            this.llm = llm;
        }
        
        public Boolean getModeration() {
            return moderation;
        }
        
        public void setModeration(Boolean moderation) {
            this.moderation = moderation;
        }
        
        public Boolean getRerank() {
            return rerank;
        }
        
        public void setRerank(Boolean rerank) {
            this.rerank = rerank;
        }
        
        public Boolean getSpeech2text() {
            return speech2text;
        }
        
        public void setSpeech2text(Boolean speech2text) {
            this.speech2text = speech2text;
        }
        
        public Boolean getTextEmbedding() {
            return textEmbedding;
        }
        
        public void setTextEmbedding(Boolean textEmbedding) {
            this.textEmbedding = textEmbedding;
        }
        
        public Boolean getTts() {
            return tts;
        }
        
        public void setTts(Boolean tts) {
            this.tts = tts;
        }
    }
    
    /**
     * 工具权限配置
     */
    public static class ToolPermission {
        private Boolean enabled;
        
        public Boolean getEnabled() {
            return enabled;
        }
        
        public void setEnabled(Boolean enabled) {
            this.enabled = enabled;
        }
    }
}





