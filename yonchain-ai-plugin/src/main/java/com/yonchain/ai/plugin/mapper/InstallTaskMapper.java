package com.yonchain.ai.plugin.mapper;

import com.yonchain.ai.plugin.entity.InstallTask;
import org.apache.ibatis.annotations.*;

import java.util.List;

/**
 * 安装任务 Mapper 接口
 * 
 * @author yonchain
 */
@Mapper
public interface InstallTaskMapper {

    /**
     * 插入安装任务
     * 
     * @param installTask 安装任务
     * @return 影响行数
     */
    @Insert("INSERT INTO install_tasks (id, tenant_id, status, total_plugins, completed_plugins, plugins, error_message, created_at, updated_at) " +
            "VALUES (#{id}, #{tenantId}, #{status}, #{totalPlugins}, #{completedPlugins}, #{plugins}, #{errorMessage}, #{createdAt}, #{updatedAt})")
    int insert(InstallTask installTask);

    /**
     * 根据ID查询安装任务
     * 
     * @param id 任务ID
     * @return 安装任务
     */
    @Select("SELECT * FROM install_tasks WHERE id = #{id}")
    InstallTask findById(@Param("id") String id);

    /**
     * 根据租户ID查询安装任务列表
     * 
     * @param tenantId 租户ID
     * @return 安装任务列表
     */
    @Select("SELECT * FROM install_tasks WHERE tenant_id = #{tenantId} ORDER BY created_at DESC")
    List<InstallTask> findByTenantId(@Param("tenantId") String tenantId);

    /**
     * 根据状态查询安装任务列表
     * 
     * @param status 任务状态
     * @return 安装任务列表
     */
    @Select("SELECT * FROM install_tasks WHERE status = #{status} ORDER BY created_at DESC")
    List<InstallTask> findByStatus(@Param("status") String status);

    /**
     * 根据租户ID和状态查询安装任务列表
     * 
     * @param tenantId 租户ID
     * @param status 任务状态
     * @return 安装任务列表
     */
    @Select("SELECT * FROM install_tasks WHERE tenant_id = #{tenantId} AND status = #{status} ORDER BY created_at DESC")
    List<InstallTask> findByTenantIdAndStatus(@Param("tenantId") String tenantId, @Param("status") String status);

    /**
     * 查询所有安装任务
     * 
     * @return 安装任务列表
     */
    @Select("SELECT * FROM install_tasks ORDER BY created_at DESC")
    List<InstallTask> findAll();

    /**
     * 更新安装任务状态
     * 
     * @param id 任务ID
     * @param status 新状态
     * @param updatedAt 更新时间
     * @return 影响行数
     */
    @Update("UPDATE install_tasks SET status = #{status}, updated_at = #{updatedAt} WHERE id = #{id}")
    int updateStatus(@Param("id") String id, @Param("status") String status, @Param("updatedAt") java.time.LocalDateTime updatedAt);

    /**
     * 更新安装任务进度
     * 
     * @param id 任务ID
     * @param completedPlugins 已完成插件数量
     * @param updatedAt 更新时间
     * @return 影响行数
     */
    @Update("UPDATE install_tasks SET completed_plugins = #{completedPlugins}, updated_at = #{updatedAt} WHERE id = #{id}")
    int updateProgress(@Param("id") String id, @Param("completedPlugins") Long completedPlugins, @Param("updatedAt") java.time.LocalDateTime updatedAt);

    /**
     * 更新安装任务错误信息
     * 
     * @param id 任务ID
     * @param status 任务状态
     * @param errorMessage 错误信息
     * @param updatedAt 更新时间
     * @return 影响行数
     */
    @Update("UPDATE install_tasks SET status = #{status}, error_message = #{errorMessage}, updated_at = #{updatedAt} WHERE id = #{id}")
    int updateError(@Param("id") String id, @Param("status") String status, @Param("errorMessage") String errorMessage, @Param("updatedAt") java.time.LocalDateTime updatedAt);

    /**
     * 完成安装任务
     * 
     * @param id 任务ID
     * @param totalPlugins 总插件数量
     * @param updatedAt 更新时间
     * @return 影响行数
     */
    @Update("UPDATE install_tasks SET status = 'completed', completed_plugins = #{totalPlugins}, updated_at = #{updatedAt} WHERE id = #{id}")
    int complete(@Param("id") String id, @Param("totalPlugins") Long totalPlugins, @Param("updatedAt") java.time.LocalDateTime updatedAt);

    /**
     * 更新安装任务（完整更新）
     * 
     * @param installTask 安装任务
     * @return 影响行数
     */
    @Update("UPDATE install_tasks SET tenant_id = #{tenantId}, status = #{status}, total_plugins = #{totalPlugins}, " +
            "completed_plugins = #{completedPlugins}, plugins = #{plugins}, error_message = #{errorMessage}, " +
            "updated_at = #{updatedAt} WHERE id = #{id}")
    int update(InstallTask installTask);

    /**
     * 根据ID删除安装任务
     * 
     * @param id 任务ID
     * @return 影响行数
     */
    @Delete("DELETE FROM install_tasks WHERE id = #{id}")
    int deleteById(@Param("id") String id);

    /**
     * 根据租户ID删除安装任务
     * 
     * @param tenantId 租户ID
     * @return 影响行数
     */
    @Delete("DELETE FROM install_tasks WHERE tenant_id = #{tenantId}")
    int deleteByTenantId(@Param("tenantId") String tenantId);

    /**
     * 统计安装任务总数
     * 
     * @return 任务总数
     */
    @Select("SELECT COUNT(*) FROM install_tasks")
    long count();

    /**
     * 根据租户ID统计安装任务数量
     * 
     * @param tenantId 租户ID
     * @return 任务数量
     */
    @Select("SELECT COUNT(*) FROM install_tasks WHERE tenant_id = #{tenantId}")
    long countByTenantId(@Param("tenantId") String tenantId);

    /**
     * 根据状态统计安装任务数量
     * 
     * @param status 任务状态
     * @return 任务数量
     */
    @Select("SELECT COUNT(*) FROM install_tasks WHERE status = #{status}")
    long countByStatus(@Param("status") String status);

    /**
     * 检查任务是否存在
     * 
     * @param id 任务ID
     * @return 是否存在
     */
    @Select("SELECT COUNT(*) > 0 FROM install_tasks WHERE id = #{id}")
    boolean existsById(@Param("id") String id);
}




