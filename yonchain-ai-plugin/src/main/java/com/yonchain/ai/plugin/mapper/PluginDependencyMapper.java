package com.yonchain.ai.plugin.mapper;

import com.yonchain.ai.plugin.entity.PluginDependencyEntity;
import org.apache.ibatis.annotations.*;

import java.util.List;

/**
 * 插件依赖数据访问接口
 * 
 * @author yonchain
 */
@Mapper
public interface PluginDependencyMapper {
    
    /**
     * 插入插件依赖记录
     * 
     * @param dependency 插件依赖实体
     * @return 影响行数
     */
    @Insert("INSERT INTO plugin_dependencies (plugin_id, dependency_id, min_version, max_version, optional) " +
            "VALUES (#{pluginId}, #{dependencyId}, #{minVersion}, #{maxVersion}, #{optional})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(PluginDependencyEntity dependency);
    
    /**
     * 根据ID删除插件依赖记录
     * 
     * @param id 依赖记录ID
     * @return 影响行数
     */
    @Delete("DELETE FROM plugin_dependencies WHERE id = #{id}")
    int deleteById(Long id);
    
    /**
     * 根据插件ID删除所有依赖记录
     * 
     * @param pluginId 插件ID
     * @return 影响行数
     */
    @Delete("DELETE FROM plugin_dependencies WHERE plugin_id = #{pluginId}")
    int deleteByPluginId(String pluginId);
    
    /**
     * 更新插件依赖记录
     * 
     * @param dependency 插件依赖实体
     * @return 影响行数
     */
    @Update("UPDATE plugin_dependencies SET " +
            "dependency_id = #{dependencyId}, " +
            "min_version = #{minVersion}, " +
            "max_version = #{maxVersion}, " +
            "optional = #{optional} " +
            "WHERE id = #{id}")
    int update(PluginDependencyEntity dependency);
    
    /**
     * 根据ID查询插件依赖记录
     * 
     * @param id 依赖记录ID
     * @return 插件依赖实体
     */
    @Select("SELECT id, plugin_id, dependency_id, min_version, max_version, optional " +
            "FROM plugin_dependencies WHERE id = #{id}")
    @Results({
        @Result(property = "id", column = "id"),
        @Result(property = "pluginId", column = "plugin_id"),
        @Result(property = "dependencyId", column = "dependency_id"),
        @Result(property = "minVersion", column = "min_version"),
        @Result(property = "maxVersion", column = "max_version"),
        @Result(property = "optional", column = "optional")
    })
    PluginDependencyEntity selectById(Long id);
    
    /**
     * 根据插件ID查询所有依赖记录
     * 
     * @param pluginId 插件ID
     * @return 插件依赖实体列表
     */
    @Select("SELECT id, plugin_id, dependency_id, min_version, max_version, optional " +
            "FROM plugin_dependencies WHERE plugin_id = #{pluginId}")
    @Results({
        @Result(property = "id", column = "id"),
        @Result(property = "pluginId", column = "plugin_id"),
        @Result(property = "dependencyId", column = "dependency_id"),
        @Result(property = "minVersion", column = "min_version"),
        @Result(property = "maxVersion", column = "max_version"),
        @Result(property = "optional", column = "optional")
    })
    List<PluginDependencyEntity> selectByPluginId(String pluginId);
    
    /**
     * 查询所有插件依赖记录
     * 
     * @return 插件依赖实体列表
     */
    @Select("SELECT id, plugin_id, dependency_id, min_version, max_version, optional " +
            "FROM plugin_dependencies ORDER BY plugin_id, dependency_id")
    @Results({
        @Result(property = "id", column = "id"),
        @Result(property = "pluginId", column = "plugin_id"),
        @Result(property = "dependencyId", column = "dependency_id"),
        @Result(property = "minVersion", column = "min_version"),
        @Result(property = "maxVersion", column = "max_version"),
        @Result(property = "optional", column = "optional")
    })
    List<PluginDependencyEntity> selectAll();
    
    /**
     * 根据依赖插件ID查询哪些插件依赖它
     * 
     * @param dependencyId 依赖插件ID
     * @return 插件依赖实体列表
     */
    @Select("SELECT id, plugin_id, dependency_id, min_version, max_version, optional " +
            "FROM plugin_dependencies WHERE dependency_id = #{dependencyId}")
    @Results({
        @Result(property = "id", column = "id"),
        @Result(property = "pluginId", column = "plugin_id"),
        @Result(property = "dependencyId", column = "dependency_id"),
        @Result(property = "minVersion", column = "min_version"),
        @Result(property = "maxVersion", column = "max_version"),
        @Result(property = "optional", column = "optional")
    })
    List<PluginDependencyEntity> selectByDependencyId(String dependencyId);
    
    /**
     * 检查是否存在特定的依赖关系
     * 
     * @param pluginId 插件ID
     * @param dependencyId 依赖插件ID
     * @return 依赖记录数量
     */
    @Select("SELECT COUNT(*) FROM plugin_dependencies " +
            "WHERE plugin_id = #{pluginId} AND dependency_id = #{dependencyId}")
    int countByPluginAndDependency(@Param("pluginId") String pluginId, @Param("dependencyId") String dependencyId);
}
