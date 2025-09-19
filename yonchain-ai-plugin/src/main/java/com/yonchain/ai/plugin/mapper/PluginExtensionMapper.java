package com.yonchain.ai.plugin.mapper;

import com.yonchain.ai.plugin.entity.PluginExtensionEntity;
import org.apache.ibatis.annotations.*;

import java.util.List;

/**
 * 插件扩展点数据访问接口
 * 
 * @author yonchain
 */
@Mapper
public interface PluginExtensionMapper {
    
    /**
     * 插入插件扩展点记录
     * 
     * @param extension 插件扩展点实体
     * @return 影响行数
     */
    @Insert("INSERT INTO plugin_extensions (plugin_id, point, implementation) " +
            "VALUES (#{pluginId}, #{point}, #{implementation})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(PluginExtensionEntity extension);
    
    /**
     * 根据ID删除插件扩展点记录
     * 
     * @param id 扩展点记录ID
     * @return 影响行数
     */
    @Delete("DELETE FROM plugin_extensions WHERE id = #{id}")
    int deleteById(Long id);
    
    /**
     * 根据插件ID删除所有扩展点记录
     * 
     * @param pluginId 插件ID
     * @return 影响行数
     */
    @Delete("DELETE FROM plugin_extensions WHERE plugin_id = #{pluginId}")
    int deleteByPluginId(String pluginId);
    
    /**
     * 更新插件扩展点记录
     * 
     * @param extension 插件扩展点实体
     * @return 影响行数
     */
    @Update("UPDATE plugin_extensions SET " +
            "point = #{point}, " +
            "implementation = #{implementation} " +
            "WHERE id = #{id}")
    int update(PluginExtensionEntity extension);
    
    /**
     * 根据ID查询插件扩展点记录
     * 
     * @param id 扩展点记录ID
     * @return 插件扩展点实体
     */
    @Select("SELECT id, plugin_id, point, implementation " +
            "FROM plugin_extensions WHERE id = #{id}")
    @Results({
        @Result(property = "id", column = "id"),
        @Result(property = "pluginId", column = "plugin_id"),
        @Result(property = "point", column = "point"),
        @Result(property = "implementation", column = "implementation")
    })
    PluginExtensionEntity selectById(Long id);
    
    /**
     * 根据插件ID查询所有扩展点记录
     * 
     * @param pluginId 插件ID
     * @return 插件扩展点实体列表
     */
    @Select("SELECT id, plugin_id, point, implementation " +
            "FROM plugin_extensions WHERE plugin_id = #{pluginId}")
    @Results({
        @Result(property = "id", column = "id"),
        @Result(property = "pluginId", column = "plugin_id"),
        @Result(property = "point", column = "point"),
        @Result(property = "implementation", column = "implementation")
    })
    List<PluginExtensionEntity> selectByPluginId(String pluginId);
    
    /**
     * 根据扩展点名称查询所有相关记录
     * 
     * @param point 扩展点名称
     * @return 插件扩展点实体列表
     */
    @Select("SELECT id, plugin_id, point, implementation " +
            "FROM plugin_extensions WHERE point = #{point}")
    @Results({
        @Result(property = "id", column = "id"),
        @Result(property = "pluginId", column = "plugin_id"),
        @Result(property = "point", column = "point"),
        @Result(property = "implementation", column = "implementation")
    })
    List<PluginExtensionEntity> selectByPoint(String point);
    
    /**
     * 查询所有插件扩展点记录
     * 
     * @return 插件扩展点实体列表
     */
    @Select("SELECT id, plugin_id, point, implementation " +
            "FROM plugin_extensions ORDER BY plugin_id, point")
    @Results({
        @Result(property = "id", column = "id"),
        @Result(property = "pluginId", column = "plugin_id"),
        @Result(property = "point", column = "point"),
        @Result(property = "implementation", column = "implementation")
    })
    List<PluginExtensionEntity> selectAll();
    
    /**
     * 检查是否存在特定的扩展点实现
     * 
     * @param pluginId 插件ID
     * @param point 扩展点名称
     * @return 扩展点记录数量
     */
    @Select("SELECT COUNT(*) FROM plugin_extensions " +
            "WHERE plugin_id = #{pluginId} AND point = #{point}")
    int countByPluginAndPoint(@Param("pluginId") String pluginId, @Param("point") String point);
    
    /**
     * 根据实现类查询扩展点记录
     * 
     * @param implementation 实现类名
     * @return 插件扩展点实体列表
     */
    @Select("SELECT id, plugin_id, point, implementation " +
            "FROM plugin_extensions WHERE implementation = #{implementation}")
    @Results({
        @Result(property = "id", column = "id"),
        @Result(property = "pluginId", column = "plugin_id"),
        @Result(property = "point", column = "point"),
        @Result(property = "implementation", column = "implementation")
    })
    List<PluginExtensionEntity> selectByImplementation(String implementation);
}
