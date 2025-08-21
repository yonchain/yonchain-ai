package com.yonchain.ai.model.dto;

import com.yonchain.ai.api.model.ModelProvider;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * 模型提供商DTO实现类
 */
@Data
public class ModelProviderDto implements ModelProvider {
    
    private String id;
    private String tenantId;
    private String providerName;
    private String providerType;
    private String encryptedConfig;
    private LocalDateTime lastUsed;
    private String quotaType;
    private Long quotaLimit;
    private Long quotaUsed;
    private Boolean isValid;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    
    // 扩展字段，用于我们的混合配置架构
    private String code;
    private String name;
    private String description;
    private String icon;
    private String website;
    private String apiDocUrl;
    private List<String> supportedFeatures;
    private Map<String, Object> parameterSchema;
    private boolean configured;
    private Boolean enabled;
    private String apiKey;
    private String baseUrl;
    private String proxyUrl;
    private Map<String, Object> customConfig;
}