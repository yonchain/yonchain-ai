package com.yonchain.ai.model.mapper;

import com.yonchain.ai.model.entity.AiModel;
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
    AiModel selectById(@Param("id") String id);

    /**
     * 根据租户ID查询模型列表
     *
     * @param tenantId 租户ID
     * @param params   查询参数
     * @return 模型列表
     */
    List<AiModel> selectList(@Param("tenantId") String tenantId, @Param("params") Map<String, Object> params);

    /**
     * 插入模型
     *
     * @param model 模型实体
     * @return 影响行数
     */
    void insert(AiModel model);

    /**
     * 更新模型
     *
     * @param model 模型实体
     * @return 影响行数
     */
    void update(AiModel model);

    /**
     * 删除模型
     *
     * @param id 模型ID
     * @return 影响行数
     */
    void deleteById(@Param("id") String id);
    
    /**
     * 设置默认模型
     *
     * @param modelId 模型ID
     * @param tenantId 租户ID
     */
    void setTenantDefaultAiModel(@Param("modelId") String modelId, @Param("tenantId") String tenantId);
    
    /**
     * 清除租户的所有默认模型
     *
     * @param tenantId 租户ID
     */
    void clearTenantDefaultAiModels(@Param("tenantId") String tenantId);
    
    /**
     * 获取租户的默认模型
     *
     * @param tenantId 租户ID
     * @return 默认模型
     */
    AiModel getTenantDefaultAiModel(@Param("tenantId") String tenantId);
}
