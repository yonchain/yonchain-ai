package com.yonchain.ai.plugin.mapper;

import com.yonchain.ai.plugin.entity.PluginEntity;
import org.apache.ibatis.annotations.*;

import java.util.List;

/**
 * 插件信息数据访问接口
 * 
 * @author yonchain
 */
@Mapper
public interface PluginMapper {
    
    /**
     * 插入插件记录
     * 
     * @param plugin 插件实体
     * @return 影响行数
     */
    @Insert("INSERT INTO plugin_info (plugin_id, name, version, description, author, homepage, " +
            "type, status, plugin_path, main_class, provider_source, provider_interface, " +
            "created_at, updated_at, installed_at, enabled_at, disabled_at,icon_path) " +
            "VALUES (#{pluginId}, #{name}, #{version}, #{description}, #{author}, #{homepage}, " +
            "#{type}, #{status}, #{pluginPath}, #{mainClass}, #{providerSource}, #{providerInterface}, " +
            "#{createdAt}, #{updatedAt}, #{installedAt}, #{enabledAt}, #{disabledAt},#{iconPath})")
    int insert(PluginEntity plugin);
    
    /**
     * 根据插件ID删除插件记录
     * 
     * @param pluginId 插件ID
     * @return 影响行数
     */
    @Delete("DELETE FROM plugin_info WHERE plugin_id = #{pluginId}")
    int deleteById(String pluginId);
    
    /**
     * 更新插件记录
     * 
     * @param plugin 插件实体
     * @return 影响行数
     */
    @Update("UPDATE plugin_info SET " +
            "name = #{name}, " +
            "version = #{version}, " +
            "description = #{description}, " +
            "author = #{author}, " +
            "homepage = #{homepage}, " +
            "type = #{type}, " +
            "status = #{status}, " +
            "plugin_path = #{pluginPath}, " +
            "main_class = #{mainClass}, " +
            "provider_source = #{providerSource}, " +
            "provider_interface = #{providerInterface}, " +
            "updated_at = #{updatedAt}, " +
            "installed_at = #{installedAt}, " +
            "enabled_at = #{enabledAt}, " +
            "disabled_at = #{disabledAt}, " +
            "icon_path = #{iconPath} " +
            "WHERE plugin_id = #{pluginId}")
    int update(PluginEntity plugin);
    
    /**
     * 更新插件状态
     * 
     * @param pluginId 插件ID
     * @param status 新状态
     * @return 影响行数
     */
    @Update("UPDATE plugin_info SET status = #{status}, updated_at = CURRENT_TIMESTAMP " +
            "WHERE plugin_id = #{pluginId}")
    int updateStatus(@Param("pluginId") String pluginId, @Param("status") String status);
    
    /**
     * 根据插件ID查询插件记录
     * 
     * @param pluginId 插件ID
     * @return 插件实体
     */
    @Select("SELECT plugin_id, name, version, description, author, homepage, " +
            "type, status, plugin_path, main_class, provider_source, provider_interface, " +
            "created_at, updated_at, installed_at, enabled_at, disabled_at,icon_path " +
            "FROM plugin_info WHERE plugin_id = #{pluginId}")
    @Results({
        @Result(property = "pluginId", column = "plugin_id"),
        @Result(property = "name", column = "name"),
        @Result(property = "version", column = "version"),
        @Result(property = "description", column = "description"),
        @Result(property = "author", column = "author"),
        @Result(property = "homepage", column = "homepage"),
        @Result(property = "type", column = "type"),
        @Result(property = "status", column = "status"),
        @Result(property = "pluginPath", column = "plugin_path"),
        @Result(property = "mainClass", column = "main_class"),
        @Result(property = "providerSource", column = "provider_source"),
        @Result(property = "providerInterface", column = "provider_interface"),
        @Result(property = "createdAt", column = "created_at"),
        @Result(property = "updatedAt", column = "updated_at"),
        @Result(property = "installedAt", column = "installed_at"),
        @Result(property = "enabledAt", column = "enabled_at"),
        @Result(property = "disabledAt", column = "disabled_at"),
        @Result(property = "iconPath", column = "icon_path")

    })
    PluginEntity selectById(String pluginId);
    
    /**
     * 查询所有插件记录
     * 
     * @return 插件实体列表
     */
    @Select("SELECT plugin_id, name, version, description, author, homepage, " +
            "type, status, plugin_path, main_class, provider_source, provider_interface, " +
            "created_at, updated_at, installed_at, enabled_at, disabled_at,icon_path " +
            "FROM plugin_info ORDER BY created_at DESC")
    @Results({
        @Result(property = "pluginId", column = "plugin_id"),
        @Result(property = "name", column = "name"),
        @Result(property = "version", column = "version"),
        @Result(property = "description", column = "description"),
        @Result(property = "author", column = "author"),
        @Result(property = "homepage", column = "homepage"),
        @Result(property = "type", column = "type"),
        @Result(property = "status", column = "status"),
        @Result(property = "pluginPath", column = "plugin_path"),
        @Result(property = "mainClass", column = "main_class"),
        @Result(property = "providerSource", column = "provider_source"),
        @Result(property = "providerInterface", column = "provider_interface"),
        @Result(property = "createdAt", column = "created_at"),
        @Result(property = "updatedAt", column = "updated_at"),
        @Result(property = "installedAt", column = "installed_at"),
        @Result(property = "enabledAt", column = "enabled_at"),
        @Result(property = "disabledAt", column = "disabled_at"),
        @Result(property = "iconPath", column = "icon_path")
    })
    List<PluginEntity> selectAll();
    
    /**
     * 根据插件类型查询插件记录
     * 
     * @param type 插件类型
     * @return 插件实体列表
     */
    @Select("SELECT plugin_id, name, version, description, author, homepage, " +
            "type, status, plugin_path, main_class, provider_source, provider_interface, " +
            "created_at, updated_at, installed_at, enabled_at, disabled_at " +
            "FROM plugin_info WHERE type = #{type} ORDER BY created_at DESC")
    @Results({
        @Result(property = "pluginId", column = "plugin_id"),
        @Result(property = "name", column = "name"),
        @Result(property = "version", column = "version"),
        @Result(property = "description", column = "description"),
        @Result(property = "author", column = "author"),
        @Result(property = "homepage", column = "homepage"),
        @Result(property = "type", column = "type"),
        @Result(property = "status", column = "status"),
        @Result(property = "pluginPath", column = "plugin_path"),
        @Result(property = "mainClass", column = "main_class"),
        @Result(property = "providerSource", column = "provider_source"),
        @Result(property = "providerInterface", column = "provider_interface"),
        @Result(property = "createdAt", column = "created_at"),
        @Result(property = "updatedAt", column = "updated_at"),
        @Result(property = "installedAt", column = "installed_at"),
        @Result(property = "enabledAt", column = "enabled_at"),
        @Result(property = "disabledAt", column = "disabled_at")
    })
    List<PluginEntity> selectByType(String type);
    
    /**
     * 根据插件状态查询插件记录
     * 
     * @param status 插件状态
     * @return 插件实体列表
     */
    @Select("SELECT plugin_id, name, version, description, author, homepage, " +
            "type, status, plugin_path, main_class, provider_source, provider_interface, " +
            "created_at, updated_at, installed_at, enabled_at, disabled_at " +
            "FROM plugin_info WHERE status = #{status} ORDER BY created_at DESC")
    @Results({
        @Result(property = "pluginId", column = "plugin_id"),
        @Result(property = "name", column = "name"),
        @Result(property = "version", column = "version"),
        @Result(property = "description", column = "description"),
        @Result(property = "author", column = "author"),
        @Result(property = "homepage", column = "homepage"),
        @Result(property = "type", column = "type"),
        @Result(property = "status", column = "status"),
        @Result(property = "pluginPath", column = "plugin_path"),
        @Result(property = "mainClass", column = "main_class"),
        @Result(property = "providerSource", column = "provider_source"),
        @Result(property = "providerInterface", column = "provider_interface"),
        @Result(property = "createdAt", column = "created_at"),
        @Result(property = "updatedAt", column = "updated_at"),
        @Result(property = "installedAt", column = "installed_at"),
        @Result(property = "enabledAt", column = "enabled_at"),
        @Result(property = "disabledAt", column = "disabled_at")
    })
    List<PluginEntity> selectByStatus(String status);
    
    /**
     * 根据插件名称模糊查询
     * 
     * @param name 插件名称（支持模糊匹配）
     * @return 插件实体列表
     */
    @Select("SELECT plugin_id, name, version, description, author, homepage, " +
            "type, status, plugin_path, main_class, provider_source, provider_interface, " +
            "created_at, updated_at, installed_at, enabled_at, disabled_at " +
            "FROM plugin_info WHERE name LIKE CONCAT('%', #{name}, '%') ORDER BY created_at DESC")
    @Results({
        @Result(property = "pluginId", column = "plugin_id"),
        @Result(property = "name", column = "name"),
        @Result(property = "version", column = "version"),
        @Result(property = "description", column = "description"),
        @Result(property = "author", column = "author"),
        @Result(property = "homepage", column = "homepage"),
        @Result(property = "type", column = "type"),
        @Result(property = "status", column = "status"),
        @Result(property = "pluginPath", column = "plugin_path"),
        @Result(property = "mainClass", column = "main_class"),
        @Result(property = "providerSource", column = "provider_source"),
        @Result(property = "providerInterface", column = "provider_interface"),
        @Result(property = "createdAt", column = "created_at"),
        @Result(property = "updatedAt", column = "updated_at"),
        @Result(property = "installedAt", column = "installed_at"),
        @Result(property = "enabledAt", column = "enabled_at"),
        @Result(property = "disabledAt", column = "disabled_at")
    })
    List<PluginEntity> selectByNameLike(String name);
    
    /**
     * 检查插件是否存在
     * 
     * @param pluginId 插件ID
     * @return 存在的记录数量
     */
    @Select("SELECT COUNT(*) FROM plugin_info WHERE plugin_id = #{pluginId}")
    int countById(String pluginId);
    
    /**
     * 检查插件是否存在（布尔返回值）
     * 
     * @param pluginId 插件ID
     * @return 是否存在
     */
    default boolean existsById(String pluginId) {
        return countById(pluginId) > 0;
    }
    
    /**
     * 根据插件ID查询插件记录（别名方法）
     * 
     * @param pluginId 插件ID
     * @return 插件实体
     */
    default PluginEntity findById(String pluginId) {
        return selectById(pluginId);
    }
    
    /**
     * 查询所有插件记录（别名方法）
     * 
     * @return 插件实体列表
     */
    default List<PluginEntity> findAll() {
        return selectAll();
    }
    
    /**
     * 根据插件类型查询插件记录（别名方法）
     * 
     * @param type 插件类型
     * @return 插件实体列表
     */
    default List<PluginEntity> findByType(String type) {
        return selectByType(type);
    }
    
    /**
     * 根据插件状态查询插件记录（别名方法）
     * 
     * @param status 插件状态
     * @return 插件实体列表
     */
    default List<PluginEntity> findByStatus(String status) {
        return selectByStatus(status);
    }
    
    /**
     * 删除插件记录（别名方法）
     * 
     * @param pluginId 插件ID
     * @return 影响行数
     */
    default int deletePlugin(String pluginId) {
        return deleteById(pluginId);
    }
    
    /**
     * 统计所有插件数量
     * 
     * @return 插件总数
     */
    @Select("SELECT COUNT(*) FROM plugin_info")
    long countAll();
    
    /**
     * 根据状态统计插件数量
     * 
     * @param status 插件状态
     * @return 插件数量
     */
    @Select("SELECT COUNT(*) FROM plugin_info WHERE status = #{status}")
    long countByStatus(String status);
}
