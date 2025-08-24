package com.yonchain.ai.model.mapper;

import com.yonchain.ai.model.entity.ModelEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * 模型Mapper接口
 *
 * @author chengy
 * @since 1.0.0
 */
@Mapper
public interface ModelMapper {
    /**
     * 根据ID查询模型
     *
     * @param id 模型ID
     * @return 模型实体
     */
    ModelEntity selectById(@Param("id") String id);

    /**
     * 根据租户ID查询模型列表
     *
     * @param tenantId 租户ID
     * @param params   查询参数
     * @return 模型列表
     */
    List<ModelEntity> selectList(@Param("tenantId") String tenantId, @Param("params") Map<String, Object> params);

    /**
     * 插入模型
     *
     * @param model 模型实体
     * @return 影响行数
     */
    void insert(ModelEntity model);

    /**
     * 更新模型
     *
     * @param model 模型实体
     * @return 影响行数
     */
    void update(ModelEntity model);

    /**
     * 删除模型
     *
     * @param id 模型ID
     * @return 影响行数
     */
    void deleteById(@Param("id") String id);
    
    /**
     * 根据租户ID和模型代码查询配置
     *
     * @param tenantId 租户ID
     * @param modelCode 模型代码
     * @return 模型实体
     */
    ModelEntity selectByTenantAndModelCode(@Param("tenantId") String tenantId, @Param("modelCode") String modelCode);
    
    /**
     * 根据租户ID、提供商代码和模型代码查询配置
     *
     * @param tenantId 租户ID
     * @param providerCode 提供商代码
     * @param modelCode 模型代码
     * @return 模型实体
     */
    ModelEntity selectByTenantProviderAndModelCode(@Param("tenantId") String tenantId, @Param("providerCode") String providerCode, @Param("modelCode") String modelCode);

    /**
     * 根据租户ID和提供商代码查询模型列表
     *
     * @param tenantId 租户ID
     * @param providerCode 提供商代码
     * @return 模型列表
     */
    List<ModelEntity> selectByTenantAndProviderCode(@Param("tenantId") String tenantId, @Param("providerCode") String providerCode);

    /**
     * 根据启用状态查询模型列表
     *
     * @param tenantId 租户ID
     * @param enabled 是否启用
     * @return 模型列表
     */
    List<ModelEntity> selectByEnabled(@Param("tenantId") String tenantId, @Param("enabled") Boolean enabled);

    /**
     * 根据租户ID删除所有模型配置
     *
     * @param tenantId 租户ID
     * @return 影响行数
     */
    int deleteByTenantId(@Param("tenantId") String tenantId);
}
