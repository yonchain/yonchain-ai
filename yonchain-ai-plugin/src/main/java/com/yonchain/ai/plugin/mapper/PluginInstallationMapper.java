package com.yonchain.ai.plugin.mapper;

import com.yonchain.ai.plugin.entity.PluginInstallation;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 插件安装 Mapper 接口
 * 
 * @author yonchain
 */
@Mapper
public interface PluginInstallationMapper {

    /**
     * 插入插件安装记录
     * 
     * @param pluginInstallation 插件安装记录
     * @return 影响行数
     */
    int insert(PluginInstallation pluginInstallation);

    /**
     * 根据ID查询插件安装记录
     * 
     * @param id 记录ID
     * @return 插件安装记录
     */
    PluginInstallation findById(@Param("id") String id);

    /**
     * 根据租户ID查询插件安装记录列表
     * 
     * @param tenantId 租户ID
     * @return 插件安装记录列表
     */
    List<PluginInstallation> findByTenantId(@Param("tenantId") String tenantId);

    /**
     * 根据插件ID查询插件安装记录列表
     * 
     * @param pluginId 插件ID
     * @return 插件安装记录列表
     */
    List<PluginInstallation> findByPluginId(@Param("pluginId") String pluginId);

    /**
     * 根据租户ID和插件ID查询插件安装记录
     * 
     * @param tenantId 租户ID
     * @param pluginId 插件ID
     * @return 插件安装记录
     */
    PluginInstallation findByTenantIdAndPluginId(@Param("tenantId") String tenantId, @Param("pluginId") String pluginId);

    /**
     * 根据插件唯一标识符查询插件安装记录
     * 
     * @param pluginUniqueIdentifier 插件唯一标识符
     * @return 插件安装记录
     */
    PluginInstallation findByPluginUniqueIdentifier(@Param("pluginUniqueIdentifier") String pluginUniqueIdentifier);

    /**
     * 根据运行时类型查询插件安装记录列表
     * 
     * @param runtimeType 运行时类型
     * @return 插件安装记录列表
     */
    List<PluginInstallation> findByRuntimeType(@Param("runtimeType") String runtimeType);

    /**
     * 根据租户ID和运行时类型查询插件安装记录列表
     * 
     * @param tenantId 租户ID
     * @param runtimeType 运行时类型
     * @return 插件安装记录列表
     */
    List<PluginInstallation> findByTenantIdAndRuntimeType(@Param("tenantId") String tenantId, @Param("runtimeType") String runtimeType);

    /**
     * 查询所有插件安装记录
     * 
     * @return 插件安装记录列表
     */
    List<PluginInstallation> findAll();

    /**
     * 更新端点数量
     * 
     * @param id 记录ID
     * @param endpointsSetups 端点设置数量
     * @param endpointsActive 活跃端点数量
     * @param updatedAt 更新时间
     * @return 影响行数
     */
    int updateEndpoints(@Param("id") String id, @Param("endpointsSetups") Long endpointsSetups, @Param("endpointsActive") Long endpointsActive, @Param("updatedAt") LocalDateTime updatedAt);

    /**
     * 更新元数据
     * 
     * @param id 记录ID
     * @param meta 元数据
     * @param updatedAt 更新时间
     * @return 影响行数
     */
    int updateMeta(@Param("id") String id, @Param("meta") String meta, @Param("updatedAt") LocalDateTime updatedAt);

    /**
     * 更新插件安装记录（完整更新）
     * 
     * @param pluginInstallation 插件安装记录
     * @return 影响行数
     */
    int update(PluginInstallation pluginInstallation);

    /**
     * 根据ID删除插件安装记录
     * 
     * @param id 记录ID
     * @return 影响行数
     */
    int deleteById(@Param("id") String id);

    /**
     * 根据租户ID删除插件安装记录
     * 
     * @param tenantId 租户ID
     * @return 影响行数
     */
    int deleteByTenantId(@Param("tenantId") String tenantId);

    /**
     * 根据插件ID删除插件安装记录
     * 
     * @param pluginId 插件ID
     * @return 影响行数
     */
    int deleteByPluginId(@Param("pluginId") String pluginId);

    /**
     * 根据租户ID和插件ID删除插件安装记录
     * 
     * @param tenantId 租户ID
     * @param pluginId 插件ID
     * @return 影响行数
     */
    int deleteByTenantIdAndPluginId(@Param("tenantId") String tenantId, @Param("pluginId") String pluginId);

    /**
     * 统计插件安装记录总数
     * 
     * @return 记录总数
     */
    long count();

    /**
     * 根据租户ID统计插件安装记录数量
     * 
     * @param tenantId 租户ID
     * @return 记录数量
     */
    long countByTenantId(@Param("tenantId") String tenantId);

    /**
     * 根据插件ID统计插件安装记录数量
     * 
     * @param pluginId 插件ID
     * @return 记录数量
     */
    long countByPluginId(@Param("pluginId") String pluginId);

    /**
     * 检查记录是否存在
     * 
     * @param id 记录ID
     * @return 是否存在
     */
    boolean existsById(@Param("id") String id);

    /**
     * 检查租户和插件组合是否存在
     * 
     * @param tenantId 租户ID
     * @param pluginId 插件ID
     * @return 是否存在
     */
    boolean existsByTenantIdAndPluginId(@Param("tenantId") String tenantId, @Param("pluginId") String pluginId);

    /**
     * 检查插件唯一标识符是否存在
     * 
     * @param pluginUniqueIdentifier 插件唯一标识符
     * @return 是否存在
     */
    boolean existsByPluginUniqueIdentifier(@Param("pluginUniqueIdentifier") String pluginUniqueIdentifier);

    /**
     * 根据租户ID关联查询插件信息
     * 
     * @param tenantId 租户ID
     * @return 租户已安装的插件信息列表
     */
    List<com.yonchain.ai.plugin.entity.PluginEntity> findTenantPluginsByTenantId(@Param("tenantId") String tenantId);

    /**
     * 根据租户ID分页查询插件信息（带条件）
     * 
     * @param tenantId 租户ID
     * @param status 插件状态
     * @param type 插件类型
     * @param name 插件名称（模糊查询）
     * @param pageNum 页码
     * @param pageSize 页大小
     * @param offset 偏移量
     * @return 租户已安装的插件信息列表
     */
    List<com.yonchain.ai.plugin.entity.PluginEntity> findTenantPluginsByTenantIdWithConditions(
            @Param("tenantId") String tenantId,
            @Param("status") String status,
            @Param("type") String type,
            @Param("name") String name,
            @Param("pageNum") Integer pageNum,
            @Param("pageSize") Integer pageSize,
            @Param("offset") Integer offset
    );

    /**
     * 统计租户插件数量（带条件）
     * 
     * @param tenantId 租户ID
     * @param status 插件状态
     * @param type 插件类型
     * @param name 插件名称（模糊查询）
     * @return 插件数量
     */
    long countTenantPluginsByTenantIdWithConditions(
            @Param("tenantId") String tenantId,
            @Param("status") String status,
            @Param("type") String type,
            @Param("name") String name
    );
}
