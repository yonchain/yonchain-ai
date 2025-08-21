package com.yonchain.ai.model.mapper;

import com.yonchain.ai.model.entity.ModelInstanceConfig;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 模型实例配置Mapper接口
 */
@Mapper
public interface ModelInstanceConfigMapper {

    /**
     * 根据ID查询
     */
    ModelInstanceConfig selectById(@Param("id") String id);

    /**
     * 根据租户ID和模型代码查询
     */
    ModelInstanceConfig selectByTenantAndCode(@Param("tenantId") String tenantId, 
                                            @Param("modelCode") String modelCode);

    /**
     * 根据租户ID查询所有模型配置
     */
    List<ModelInstanceConfig> selectByTenantId(@Param("tenantId") String tenantId);

    /**
     * 根据租户ID和提供商代码查询模型配置
     */
    List<ModelInstanceConfig> selectByTenantAndProvider(@Param("tenantId") String tenantId, 
                                                      @Param("providerCode") String providerCode);

    /**
     * 根据租户ID查询启用的模型配置
     */
    List<ModelInstanceConfig> selectEnabledByTenantId(@Param("tenantId") String tenantId);

    /**
     * 插入
     */
    int insert(ModelInstanceConfig modelInstanceConfig);

    /**
     * 更新
     */
    int update(ModelInstanceConfig modelInstanceConfig);

    /**
     * 根据ID删除
     */
    int deleteById(@Param("id") String id);

    /**
     * 根据租户ID和模型代码删除
     */
    int deleteByTenantAndCode(@Param("tenantId") String tenantId, 
                            @Param("modelCode") String modelCode);

    /**
     * 更新启用状态
     */
    int updateEnabled(@Param("id") String id, 
                     @Param("enabled") Boolean enabled);

    /**
     * 批量插入
     */
    int batchInsert(@Param("configs") List<ModelInstanceConfig> configs);

    /**
     * 检查是否存在
     */
    boolean existsByTenantAndCode(@Param("tenantId") String tenantId, 
                                @Param("modelCode") String modelCode);

    /**
     * 统计租户的模型数量
     */
    int countByTenantId(@Param("tenantId") String tenantId);
}