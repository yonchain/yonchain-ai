package com.yonchain.ai.model.entity;

import com.yonchain.ai.model.vo.ModelCapability;
import lombok.Data;
import lombok.experimental.Accessors;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * 模型提供商实体类
 */
@Data
@Accessors(chain = true)
public class ModelProvider {

    /**
     * 主键ID
     */
    private Long id;

    /**
     * 提供商代码，唯一标识
     */
    private String code;

    /**
     * 提供商名称
     */
    private String name;

    /**
     * 提供商描述
     */
    private String description;

    /**
     * 提供商图标URL
     */
    private String icon;

    /**
     * 提供商官网URL
     */
    private String websiteUrl;

    /**
     * 提供商类型（SYSTEM: 系统内置, CUSTOM: 自定义）
     */
    private String type;

    /**
     * 是否启用
     */
    private Boolean enabled;

    /**
     * 排序号
     */
    private Integer sortOrder;

    /**
     * API密钥
     */
    private String apiKey;

    /**
     * API基础URL
     */
    private String baseUrl;

    /**
     * API代理URL
     */
    private String proxyUrl;

    /**
     * 提供商配置参数，JSON格式
     */
  //  @TableField(typeHandler = JacksonTypeHandler.class)
    private Map<String, Object> config;

    /**
     * 提供商配置参数Schema，JSON Schema格式
     */
 //   @TableField(typeHandler = JacksonTypeHandler.class)
    private Map<String, Object> configSchema;

    /**
     * 支持的模型类型列表
     */
 //   @TableField(typeHandler = JacksonTypeHandler.class)
    private List<String> supportedModelTypes;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    private LocalDateTime updateTime;
    
    /**
     * 提供商支持的模型列表
     */
  //  @TableField(typeHandler = JacksonTypeHandler.class)
    private List<AIModel> models;
    
    /**
     * 提供商支持的能力映射，key为能力代码，value为能力对象
     */
   // @TableField(typeHandler = JacksonTypeHandler.class)
    private Map<String, ModelCapability> capabilities;
    
    /**
     * 获取提供商支持的所有模型列表
     * @return 模型列表
     */
    public List<AIModel> getModels() {
        return models;
    }
    
    /**
     * 获取提供商支持的能力映射
     * @return 能力映射
     */
    public Map<String, ModelCapability> getCapabilities() {
        return capabilities;
    }
}