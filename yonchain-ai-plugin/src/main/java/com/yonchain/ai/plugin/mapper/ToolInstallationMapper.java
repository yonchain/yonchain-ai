package com.yonchain.ai.plugin.mapper;

import com.yonchain.ai.plugin.entity.ToolInstallation;
import org.apache.ibatis.annotations.*;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 工具安装 Mapper 接口
 * 
 * @author yonchain
 */
@Mapper
public interface ToolInstallationMapper {

    /**
     * 插入工具安装记录
     * 
     * @param toolInstallation 工具安装记录
     * @return 影响行数
     */
    @Insert("INSERT INTO tool_installations (id, tenant_id, tool_name, plugin_unique_identifier, plugin_id, config, enabled, created_at, updated_at) " +
            "VALUES (#{id}, #{tenantId}, #{toolName}, #{pluginUniqueIdentifier}, #{pluginId}, #{config}, #{enabled}, #{createdAt}, #{updatedAt})")
    int insert(ToolInstallation toolInstallation);

    /**
     * 根据ID查询工具安装记录
     * 
     * @param id 记录ID
     * @return 工具安装记录
     */
    @Select("SELECT * FROM tool_installations WHERE id = #{id}")
    ToolInstallation findById(@Param("id") String id);

    /**
     * 根据租户ID查询工具安装记录列表
     * 
     * @param tenantId 租户ID
     * @return 工具安装记录列表
     */
    @Select("SELECT * FROM tool_installations WHERE tenant_id = #{tenantId} ORDER BY created_at DESC")
    List<ToolInstallation> findByTenantId(@Param("tenantId") String tenantId);

    /**
     * 根据工具名称查询工具安装记录列表
     * 
     * @param toolName 工具名称
     * @return 工具安装记录列表
     */
    @Select("SELECT * FROM tool_installations WHERE tool_name = #{toolName} ORDER BY created_at DESC")
    List<ToolInstallation> findByToolName(@Param("toolName") String toolName);

    /**
     * 根据插件ID查询工具安装记录列表
     * 
     * @param pluginId 插件ID
     * @return 工具安装记录列表
     */
    @Select("SELECT * FROM tool_installations WHERE plugin_id = #{pluginId} ORDER BY created_at DESC")
    List<ToolInstallation> findByPluginId(@Param("pluginId") String pluginId);

    /**
     * 根据租户ID和工具名称查询工具安装记录
     * 
     * @param tenantId 租户ID
     * @param toolName 工具名称
     * @return 工具安装记录
     */
    @Select("SELECT * FROM tool_installations WHERE tenant_id = #{tenantId} AND tool_name = #{toolName}")
    ToolInstallation findByTenantIdAndToolName(@Param("tenantId") String tenantId, @Param("toolName") String toolName);

    /**
     * 根据租户ID和插件ID查询工具安装记录列表
     * 
     * @param tenantId 租户ID
     * @param pluginId 插件ID
     * @return 工具安装记录列表
     */
    @Select("SELECT * FROM tool_installations WHERE tenant_id = #{tenantId} AND plugin_id = #{pluginId} ORDER BY created_at DESC")
    List<ToolInstallation> findByTenantIdAndPluginId(@Param("tenantId") String tenantId, @Param("pluginId") String pluginId);

    /**
     * 根据插件唯一标识符查询工具安装记录
     * 
     * @param pluginUniqueIdentifier 插件唯一标识符
     * @return 工具安装记录
     */
    @Select("SELECT * FROM tool_installations WHERE plugin_unique_identifier = #{pluginUniqueIdentifier}")
    ToolInstallation findByPluginUniqueIdentifier(@Param("pluginUniqueIdentifier") String pluginUniqueIdentifier);

    /**
     * 根据启用状态查询工具安装记录列表
     * 
     * @param enabled 是否启用
     * @return 工具安装记录列表
     */
    @Select("SELECT * FROM tool_installations WHERE enabled = #{enabled} ORDER BY created_at DESC")
    List<ToolInstallation> findByEnabled(@Param("enabled") Boolean enabled);

    /**
     * 根据租户ID和启用状态查询工具安装记录列表
     * 
     * @param tenantId 租户ID
     * @param enabled 是否启用
     * @return 工具安装记录列表
     */
    @Select("SELECT * FROM tool_installations WHERE tenant_id = #{tenantId} AND enabled = #{enabled} ORDER BY created_at DESC")
    List<ToolInstallation> findByTenantIdAndEnabled(@Param("tenantId") String tenantId, @Param("enabled") Boolean enabled);

    /**
     * 查询所有工具安装记录
     * 
     * @return 工具安装记录列表
     */
    @Select("SELECT * FROM tool_installations ORDER BY created_at DESC")
    List<ToolInstallation> findAll();

    /**
     * 更新工具配置
     * 
     * @param id 记录ID
     * @param config 配置信息
     * @param updatedAt 更新时间
     * @return 影响行数
     */
    @Update("UPDATE tool_installations SET config = #{config}, updated_at = #{updatedAt} WHERE id = #{id}")
    int updateConfig(@Param("id") String id, @Param("config") String config, @Param("updatedAt") LocalDateTime updatedAt);

    /**
     * 更新启用状态
     * 
     * @param id 记录ID
     * @param enabled 是否启用
     * @param updatedAt 更新时间
     * @return 影响行数
     */
    @Update("UPDATE tool_installations SET enabled = #{enabled}, updated_at = #{updatedAt} WHERE id = #{id}")
    int updateEnabled(@Param("id") String id, @Param("enabled") Boolean enabled, @Param("updatedAt") LocalDateTime updatedAt);

    /**
     * 更新插件唯一标识符
     * 
     * @param id 记录ID
     * @param pluginUniqueIdentifier 插件唯一标识符
     * @param updatedAt 更新时间
     * @return 影响行数
     */
    @Update("UPDATE tool_installations SET plugin_unique_identifier = #{pluginUniqueIdentifier}, updated_at = #{updatedAt} WHERE id = #{id}")
    int updatePluginUniqueIdentifier(@Param("id") String id, @Param("pluginUniqueIdentifier") String pluginUniqueIdentifier, @Param("updatedAt") LocalDateTime updatedAt);

    /**
     * 更新工具名称
     * 
     * @param id 记录ID
     * @param toolName 工具名称
     * @param updatedAt 更新时间
     * @return 影响行数
     */
    @Update("UPDATE tool_installations SET tool_name = #{toolName}, updated_at = #{updatedAt} WHERE id = #{id}")
    int updateToolName(@Param("id") String id, @Param("toolName") String toolName, @Param("updatedAt") LocalDateTime updatedAt);

    /**
     * 刷新更新时间
     * 
     * @param id 记录ID
     * @param updatedAt 更新时间
     * @return 影响行数
     */
    @Update("UPDATE tool_installations SET updated_at = #{updatedAt} WHERE id = #{id}")
    int touch(@Param("id") String id, @Param("updatedAt") LocalDateTime updatedAt);

    /**
     * 更新工具安装记录（完整更新）
     * 
     * @param toolInstallation 工具安装记录
     * @return 影响行数
     */
    @Update("UPDATE tool_installations SET tenant_id = #{tenantId}, tool_name = #{toolName}, " +
            "plugin_unique_identifier = #{pluginUniqueIdentifier}, plugin_id = #{pluginId}, " +
            "config = #{config}, enabled = #{enabled}, updated_at = #{updatedAt} WHERE id = #{id}")
    int update(ToolInstallation toolInstallation);

    /**
     * 根据ID删除工具安装记录
     * 
     * @param id 记录ID
     * @return 影响行数
     */
    @Delete("DELETE FROM tool_installations WHERE id = #{id}")
    int deleteById(@Param("id") String id);

    /**
     * 根据租户ID删除工具安装记录
     * 
     * @param tenantId 租户ID
     * @return 影响行数
     */
    @Delete("DELETE FROM tool_installations WHERE tenant_id = #{tenantId}")
    int deleteByTenantId(@Param("tenantId") String tenantId);

    /**
     * 根据工具名称删除工具安装记录
     * 
     * @param toolName 工具名称
     * @return 影响行数
     */
    @Delete("DELETE FROM tool_installations WHERE tool_name = #{toolName}")
    int deleteByToolName(@Param("toolName") String toolName);

    /**
     * 根据插件ID删除工具安装记录
     * 
     * @param pluginId 插件ID
     * @return 影响行数
     */
    @Delete("DELETE FROM tool_installations WHERE plugin_id = #{pluginId}")
    int deleteByPluginId(@Param("pluginId") String pluginId);

    /**
     * 根据租户ID和工具名称删除工具安装记录
     * 
     * @param tenantId 租户ID
     * @param toolName 工具名称
     * @return 影响行数
     */
    @Delete("DELETE FROM tool_installations WHERE tenant_id = #{tenantId} AND tool_name = #{toolName}")
    int deleteByTenantIdAndToolName(@Param("tenantId") String tenantId, @Param("toolName") String toolName);

    /**
     * 统计工具安装记录总数
     * 
     * @return 记录总数
     */
    @Select("SELECT COUNT(*) FROM tool_installations")
    long count();

    /**
     * 根据租户ID统计工具安装记录数量
     * 
     * @param tenantId 租户ID
     * @return 记录数量
     */
    @Select("SELECT COUNT(*) FROM tool_installations WHERE tenant_id = #{tenantId}")
    long countByTenantId(@Param("tenantId") String tenantId);

    /**
     * 根据工具名称统计工具安装记录数量
     * 
     * @param toolName 工具名称
     * @return 记录数量
     */
    @Select("SELECT COUNT(*) FROM tool_installations WHERE tool_name = #{toolName}")
    long countByToolName(@Param("toolName") String toolName);

    /**
     * 根据插件ID统计工具安装记录数量
     * 
     * @param pluginId 插件ID
     * @return 记录数量
     */
    @Select("SELECT COUNT(*) FROM tool_installations WHERE plugin_id = #{pluginId}")
    long countByPluginId(@Param("pluginId") String pluginId);

    /**
     * 统计启用的工具安装记录数量
     * 
     * @return 启用记录数量
     */
    @Select("SELECT COUNT(*) FROM tool_installations WHERE enabled = true")
    long countEnabled();

    /**
     * 检查记录是否存在
     * 
     * @param id 记录ID
     * @return 是否存在
     */
    @Select("SELECT COUNT(*) > 0 FROM tool_installations WHERE id = #{id}")
    boolean existsById(@Param("id") String id);

    /**
     * 检查租户和工具名称组合是否存在
     * 
     * @param tenantId 租户ID
     * @param toolName 工具名称
     * @return 是否存在
     */
    @Select("SELECT COUNT(*) > 0 FROM tool_installations WHERE tenant_id = #{tenantId} AND tool_name = #{toolName}")
    boolean existsByTenantIdAndToolName(@Param("tenantId") String tenantId, @Param("toolName") String toolName);

    /**
     * 检查插件唯一标识符是否存在
     * 
     * @param pluginUniqueIdentifier 插件唯一标识符
     * @return 是否存在
     */
    @Select("SELECT COUNT(*) > 0 FROM tool_installations WHERE plugin_unique_identifier = #{pluginUniqueIdentifier}")
    boolean existsByPluginUniqueIdentifier(@Param("pluginUniqueIdentifier") String pluginUniqueIdentifier);
}