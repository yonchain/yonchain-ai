package com.yonchain.ai.model.dto;

import com.yonchain.ai.api.model.ModelInfo;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * 模型信息DTO实现类
 */
@Data
public class ModelInfoDto implements ModelInfo {
    
    private String id;
    private String tenantId;
    private String providerName;
    private String modelName;
    private String modelType;
    private String encryptedConfig;
    private Boolean isValid;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    
    // 扩展字段，用于我们的混合配置架构
    private String code;
    private String name;
    private String description;
    private String provider;
    private String type;
    private String version;
    private Map<String, Object> capabilities;
    private Map<String, Object> parameterSchema;
    private Map<String, Object> defaultParameters;
    private Map<String, Object> limits;
    private boolean configured;
    private Boolean enabled;
    private Integer sortOrder;
    private Map<String, Object> defaultParams;
}