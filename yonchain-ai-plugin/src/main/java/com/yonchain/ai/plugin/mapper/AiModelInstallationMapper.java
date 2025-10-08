package com.yonchain.ai.plugin.mapper;

import com.yonchain.ai.plugin.entity.AiModelInstallation;
import org.apache.ibatis.annotations.*;

import java.time.LocalDateTime;
import java.util.List;

/**
 * AI模型安装 Mapper 接口
 * 
 * @author yonchain
 */
@Mapper
public interface AiModelInstallationMapper {

    /**
     * 插入AI模型安装记录
     * 
     * @param aiModelInstallation AI模型安装记录
     * @return 影响行数
     */
    @Insert("INSERT INTO plugin_model_installation (id, tenant_id, provider, plugin_unique_identifier, plugin_id, created_at, updated_at) " +
            "VALUES (#{id}::uuid" +
            ", #{tenantId}::uuid, #{provider}, #{pluginUniqueIdentifier}, #{pluginId}, #{createdAt}, #{updatedAt})")
    int insert(AiModelInstallation aiModelInstallation);

    /**
     * 根据ID查询AI模型安装记录
     * 
     * @param id 记录ID
     * @return AI模型安装记录
     */
    @Select("SELECT * FROM plugin_model_installation WHERE id = #{id}::uuid")
    AiModelInstallation findById(@Param("id") String id);

    /**
     * 根据租户ID查询AI模型安装记录列表
     * 
     * @param tenantId 租户ID
     * @return AI模型安装记录列表
     */
    @Select("SELECT * FROM plugin_model_installation WHERE tenant_id = #{tenantId}::uuid ORDER BY created_at DESC")
    List<AiModelInstallation> findByTenantId(@Param("tenantId") String tenantId);

    /**
     * 根据提供商查询AI模型安装记录列表
     * 
     * @param provider 提供商名称
     * @return AI模型安装记录列表
     */
    @Select("SELECT * FROM plugin_model_installation WHERE provider = #{provider} ORDER BY created_at DESC")
    List<AiModelInstallation> findByProvider(@Param("provider") String provider);

    /**
     * 根据插件ID查询AI模型安装记录列表
     * 
     * @param pluginId 插件ID
     * @return AI模型安装记录列表
     */
    @Select("SELECT * FROM plugin_model_installation WHERE plugin_id = #{pluginId}::uuid ORDER BY created_at DESC")
    List<AiModelInstallation> findByPluginId(@Param("pluginId") String pluginId);

    /**
     * 根据租户ID和提供商查询AI模型安装记录
     * 
     * @param tenantId 租户ID
     * @param provider 提供商名称
     * @return AI模型安装记录
     */
    @Select("SELECT * FROM plugin_model_installation WHERE tenant_id = #{tenantId}::uuid AND provider = #{provider}")
    AiModelInstallation findByTenantIdAndProvider(@Param("tenantId") String tenantId, @Param("provider") String provider);

    /**
     * 根据租户ID和插件ID查询AI模型安装记录列表
     * 
     * @param tenantId 租户ID
     * @param pluginId 插件ID
     * @return AI模型安装记录列表
     */
    @Select("SELECT * FROM plugin_model_installation WHERE tenant_id = #{tenantId}::uuid AND plugin_id = #{pluginId} ORDER BY created_at DESC")
    List<AiModelInstallation> findByTenantIdAndPluginId(@Param("tenantId") String tenantId, @Param("pluginId") String pluginId);

    /**
     * 根据插件唯一标识符查询AI模型安装记录
     * 
     * @param pluginUniqueIdentifier 插件唯一标识符
     * @return AI模型安装记录
     */
    @Select("SELECT * FROM plugin_model_installation WHERE plugin_unique_identifier = #{pluginUniqueIdentifier}")
    AiModelInstallation findByPluginUniqueIdentifier(@Param("pluginUniqueIdentifier") String pluginUniqueIdentifier);

    /**
     * 查询所有AI模型安装记录
     * 
     * @return AI模型安装记录列表
     */
    @Select("SELECT * FROM plugin_model_installation ORDER BY created_at DESC")
    List<AiModelInstallation> findAll();

    /**
     * 更新插件唯一标识符
     * 
     * @param id 记录ID
     * @param pluginUniqueIdentifier 插件唯一标识符
     * @param updatedAt 更新时间
     * @return 影响行数
     */
    @Update("UPDATE plugin_model_installation SET plugin_unique_identifier = #{pluginUniqueIdentifier}, updated_at = #{updatedAt} WHERE id = #{id}")
    int updatePluginUniqueIdentifier(@Param("id") String id, @Param("pluginUniqueIdentifier") String pluginUniqueIdentifier, @Param("updatedAt") LocalDateTime updatedAt);

    /**
     * 更新提供商
     * 
     * @param id 记录ID
     * @param provider 提供商名称
     * @param updatedAt 更新时间
     * @return 影响行数
     */
    @Update("UPDATE plugin_model_installation SET provider = #{provider}, updated_at = #{updatedAt} WHERE id = #{id}")
    int updateProvider(@Param("id") String id, @Param("provider") String provider, @Param("updatedAt") LocalDateTime updatedAt);

    /**
     * 刷新更新时间
     * 
     * @param id 记录ID
     * @param updatedAt 更新时间
     * @return 影响行数
     */
    @Update("UPDATE plugin_model_installation SET updated_at = #{updatedAt} WHERE id = #{id}")
    int touch(@Param("id") String id, @Param("updatedAt") LocalDateTime updatedAt);

    /**
     * 更新AI模型安装记录（完整更新）
     * 
     * @param aiModelInstallation AI模型安装记录
     * @return 影响行数
     */
    @Update("UPDATE plugin_model_installation SET tenant_id = #{tenantId}, provider = #{provider}, " +
            "plugin_unique_identifier = #{pluginUniqueIdentifier}, plugin_id = #{pluginId}, " +
            "updated_at = #{updatedAt} WHERE id = #{id}")
    int update(AiModelInstallation aiModelInstallation);

    /**
     * 根据ID删除AI模型安装记录
     * 
     * @param id 记录ID
     * @return 影响行数
     */
    @Delete("DELETE FROM plugin_model_installation WHERE id = #{id}")
    int deleteById(@Param("id") String id);

    /**
     * 根据租户ID删除AI模型安装记录
     * 
     * @param tenantId 租户ID
     * @return 影响行数
     */
    @Delete("DELETE FROM plugin_model_installation WHERE tenant_id = #{tenantId}")
    int deleteByTenantId(@Param("tenantId") String tenantId);

    /**
     * 根据提供商删除AI模型安装记录
     * 
     * @param provider 提供商名称
     * @return 影响行数
     */
    @Delete("DELETE FROM plugin_model_installation WHERE provider = #{provider}")
    int deleteByProvider(@Param("provider") String provider);

    /**
     * 根据插件ID删除AI模型安装记录
     * 
     * @param pluginId 插件ID
     * @return 影响行数
     */
    @Delete("DELETE FROM plugin_model_installation WHERE plugin_id = #{pluginId}")
    int deleteByPluginId(@Param("pluginId") String pluginId);

    /**
     * 根据租户ID和提供商删除AI模型安装记录
     * 
     * @param tenantId 租户ID
     * @param provider 提供商名称
     * @return 影响行数
     */
    @Delete("DELETE FROM plugin_model_installation WHERE tenant_id = #{tenantId} AND provider = #{provider}")
    int deleteByTenantIdAndProvider(@Param("tenantId") String tenantId, @Param("provider") String provider);

    /**
     * 统计AI模型安装记录总数
     * 
     * @return 记录总数
     */
    @Select("SELECT COUNT(*) FROM plugin_model_installation")
    long count();

    /**
     * 根据租户ID统计AI模型安装记录数量
     * 
     * @param tenantId 租户ID
     * @return 记录数量
     */
    @Select("SELECT COUNT(*) FROM plugin_model_installation WHERE tenant_id = #{tenantId}")
    long countByTenantId(@Param("tenantId") String tenantId);

    /**
     * 根据提供商统计AI模型安装记录数量
     * 
     * @param provider 提供商名称
     * @return 记录数量
     */
    @Select("SELECT COUNT(*) FROM plugin_model_installation WHERE provider = #{provider}")
    long countByProvider(@Param("provider") String provider);

    /**
     * 根据插件ID统计AI模型安装记录数量
     * 
     * @param pluginId 插件ID
     * @return 记录数量
     */
    @Select("SELECT COUNT(*) FROM plugin_model_installation WHERE plugin_id = #{pluginId}")
    long countByPluginId(@Param("pluginId") String pluginId);

    /**
     * 检查记录是否存在
     * 
     * @param id 记录ID
     * @return 是否存在
     */
    @Select("SELECT COUNT(*) > 0 FROM plugin_model_installation WHERE id = #{id}")
    boolean existsById(@Param("id") String id);

    /**
     * 检查租户和提供商组合是否存在
     * 
     * @param tenantId 租户ID
     * @param provider 提供商名称
     * @return 是否存在
     */
    @Select("SELECT COUNT(*) > 0 FROM plugin_model_installation WHERE tenant_id = #{tenantId} AND provider = #{provider}")
    boolean existsByTenantIdAndProvider(@Param("tenantId") String tenantId, @Param("provider") String provider);

    /**
     * 检查插件唯一标识符是否存在
     * 
     * @param pluginUniqueIdentifier 插件唯一标识符
     * @return 是否存在
     */
    @Select("SELECT COUNT(*) > 0 FROM plugin_model_installation WHERE plugin_unique_identifier = #{pluginUniqueIdentifier}")
    boolean existsByPluginUniqueIdentifier(@Param("pluginUniqueIdentifier") String pluginUniqueIdentifier);
}