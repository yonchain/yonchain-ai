package com.yonchain.ai.model.mapper;

import com.yonchain.ai.model.entity.ModelProviderConfig;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 模型提供商配置Mapper
 */
@Mapper
public interface ModelProviderConfigMapper {

    /**
     * 插入提供商配置
     */
    int insert(ModelProviderConfig config);

    /**
     * 根据ID查询
     */
    ModelProviderConfig selectById(@Param("id") String id);

    /**
     * 根据租户ID和提供商代码查询
     */
    ModelProviderConfig selectByTenantAndCode(@Param("tenantId") String tenantId, @Param("providerCode") String providerCode);

    /**
     * 根据租户ID查询所有提供商配置
     */
    List<ModelProviderConfig> selectByTenantId(@Param("tenantId") String tenantId);

    /**
     * 更新提供商配置
     */
    int update(ModelProviderConfig config);

    /**
     * 根据ID删除
     */
    int deleteById(@Param("id") String id);

    /**
     * 根据租户ID删除所有配置
     */
    int deleteByTenantId(@Param("tenantId") String tenantId);
}