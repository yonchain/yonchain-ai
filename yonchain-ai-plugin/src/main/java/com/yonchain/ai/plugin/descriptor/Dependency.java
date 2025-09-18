package com.yonchain.ai.plugin.descriptor;

/**
 * 插件依赖信息
 * 
 * @author yonchain
 */
public class Dependency {
    
    /**
     * 依赖的插件ID
     */
    private String pluginId;
    
    /**
     * 依赖ID（别名，与pluginId相同）
     */
    private String id;
    
    /**
     * 依赖版本
     */
    private String version;
    
    /**
     * 最小版本
     */
    private String minVersion;
    
    /**
     * 最大版本
     */
    private String maxVersion;
    
    /**
     * 是否为可选依赖
     */
    private boolean optional;
    
    /**
     * 依赖类型
     */
    private DependencyType type;
    
    public Dependency() {
    }
    
    public Dependency(String pluginId, boolean optional) {
        this.pluginId = pluginId;
        this.optional = optional;
        this.type = DependencyType.RUNTIME;
    }
    
    public Dependency(String pluginId, String version, boolean optional, DependencyType type) {
        this.pluginId = pluginId;
        this.version = version;
        this.optional = optional;
        this.type = type;
    }
    
    public String getPluginId() {
        return pluginId;
    }
    
    public void setPluginId(String pluginId) {
        this.pluginId = pluginId;
        this.id = pluginId; // 保持同步
    }
    
    public String getId() {
        return id != null ? id : pluginId;
    }
    
    public void setId(String id) {
        this.id = id;
        if (this.pluginId == null) {
            this.pluginId = id; // 保持同步
        }
    }
    
    public String getVersion() {
        return version;
    }
    
    public void setVersion(String version) {
        this.version = version;
    }
    
    public String getMinVersion() {
        return minVersion;
    }
    
    public void setMinVersion(String minVersion) {
        this.minVersion = minVersion;
    }
    
    public String getMaxVersion() {
        return maxVersion;
    }
    
    public void setMaxVersion(String maxVersion) {
        this.maxVersion = maxVersion;
    }
    
    public boolean isOptional() {
        return optional;
    }
    
    public void setOptional(boolean optional) {
        this.optional = optional;
    }
    
    public DependencyType getType() {
        return type != null ? type : DependencyType.RUNTIME;
    }
    
    public void setType(DependencyType type) {
        this.type = type;
    }
    
    /**
     * 检查版本是否匹配
     * 
     * @param actualVersion 实际版本
     * @return 是否匹配
     */
    public boolean isVersionMatched(String actualVersion) {
        if (version == null || version.trim().isEmpty()) {
            return true; // 没有版本要求
        }
        
        if (actualVersion == null || actualVersion.trim().isEmpty()) {
            return false;
        }
        
        // 简单的版本匹配，实际项目中可以使用更复杂的版本比较逻辑
        return version.equals(actualVersion);
    }
    
    @Override
    public String toString() {
        return "Dependency{" +
                "pluginId='" + pluginId + '\'' +
                ", version='" + version + '\'' +
                ", optional=" + optional +
                ", type=" + type +
                '}';
    }
    
    /**
     * 依赖类型枚举
     */
    public enum DependencyType {
        /**
         * 运行时依赖
         */
        RUNTIME("runtime"),
        
        /**
         * 编译时依赖
         */
        COMPILE("compile"),
        
        /**
         * 可选依赖
         */
        OPTIONAL("optional");
        
        private final String code;
        
        DependencyType(String code) {
            this.code = code;
        }
        
        public String getCode() {
            return code;
        }
        
        public static DependencyType fromCode(String code) {
            if (code == null || code.trim().isEmpty()) {
                return RUNTIME;
            }
            
            for (DependencyType type : values()) {
                if (type.code.equalsIgnoreCase(code.trim())) {
                    return type;
                }
            }
            
            return RUNTIME; // 默认值
        }
    }
}

