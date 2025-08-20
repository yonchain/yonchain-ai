package com.yonchain.ai.model.enums;

/**
 * 模型提供商类型枚举
 * 定义所有支持的AI模型提供商
 */
public enum ProviderType {
    
    /**
     * OpenAI提供商
     */
    OPENAI("openai", "OpenAI"),
    
    /**
     * DeepSeek提供商
     */
    DEEPSEEK("deepseek", "DeepSeek"),
    
    /**
     * Anthropic提供商
     */
    ANTHROPIC("anthropic", "Anthropic"),
    
    /**
     * Ollama提供商
     */
    OLLAMA("ollama", "Ollama"),
    
    /**
     * Grok提供商
     */
    GROK("grok", "Grok"),
    
    /**
     * 百度文心一言
     */
    BAIDU("baidu", "Baidu"),
    
    /**
     * 阿里通义千问
     */
    ALIBABA("alibaba", "Alibaba"),
    
    /**
     * 腾讯混元
     */
    TENCENT("tencent", "Tencent"),
    
    /**
     * 智谱AI
     */
    ZHIPU("zhipu", "ZhipuAI"),
    
    /**
     * 月之暗面Kimi
     */
    MOONSHOT("moonshot", "Moonshot");
    
    private final String code;
    private final String displayName;
    
    ProviderType(String code, String displayName) {
        this.code = code;
        this.displayName = displayName;
    }

    /**
     * 获取提供商代码
     */
    public String getCode() {
        return code;
    }

    /**
     * 获取显示名称
     */
    public String getDisplayName() {
        return displayName;
    }

    /**
     * 根据代码获取提供商类型
     * @param code 提供商代码
     * @return 提供商类型，如果不存在则返回null
     */
    public static ProviderType fromCode(String code) {
        if (code == null || code.trim().isEmpty()) {
            return null;
        }

        String normalizedCode = code.toLowerCase().trim();
        for (ProviderType type : values()) {
            if (type.code.equals(normalizedCode)) {
                return type;
            }
        }
        return null;
    }

    /**
     * 检查是否支持指定的提供商代码
     * @param code 提供商代码
     * @return 是否支持
     */
    public static boolean isSupported(String code) {
        return fromCode(code) != null;
    }

}