package com.yonchain.ai.plugin.mapper;

import com.yonchain.ai.plugin.entity.PluginServiceEntity;
import org.apache.ibatis.annotations.*;

import java.util.List;

/**
 * 插件服务数据访问接口
 * 
 * @author yonchain
 */
@Mapper
public interface PluginServiceMapper {
    
    /**
     * 插入插件服务记录
     * 
     * @param service 插件服务实体
     * @return 影响行数
     */
    @Insert("INSERT INTO plugin_services (plugin_id, service_name, service_class) " +
            "VALUES (#{pluginId}, #{serviceName}, #{serviceClass})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(PluginServiceEntity service);
    
    /**
     * 根据ID删除插件服务记录
     * 
     * @param id 服务记录ID
     * @return 影响行数
     */
    @Delete("DELETE FROM plugin_services WHERE id = #{id}")
    int deleteById(Long id);
    
    /**
     * 根据插件ID删除所有服务记录
     * 
     * @param pluginId 插件ID
     * @return 影响行数
     */
    @Delete("DELETE FROM plugin_services WHERE plugin_id = #{pluginId}")
    int deleteByPluginId(String pluginId);
    
    /**
     * 更新插件服务记录
     * 
     * @param service 插件服务实体
     * @return 影响行数
     */
    @Update("UPDATE plugin_services SET " +
            "service_name = #{serviceName}, " +
            "service_class = #{serviceClass} " +
            "WHERE id = #{id}")
    int update(PluginServiceEntity service);
    
    /**
     * 根据ID查询插件服务记录
     * 
     * @param id 服务记录ID
     * @return 插件服务实体
     */
    @Select("SELECT id, plugin_id, service_name, service_class " +
            "FROM plugin_services WHERE id = #{id}")
    @Results({
        @Result(property = "id", column = "id"),
        @Result(property = "pluginId", column = "plugin_id"),
        @Result(property = "serviceName", column = "service_name"),
        @Result(property = "serviceClass", column = "service_class")
    })
    PluginServiceEntity selectById(Long id);
    
    /**
     * 根据插件ID查询所有服务记录
     * 
     * @param pluginId 插件ID
     * @return 插件服务实体列表
     */
    @Select("SELECT id, plugin_id, service_name, service_class " +
            "FROM plugin_services WHERE plugin_id = #{pluginId}")
    @Results({
        @Result(property = "id", column = "id"),
        @Result(property = "pluginId", column = "plugin_id"),
        @Result(property = "serviceName", column = "service_name"),
        @Result(property = "serviceClass", column = "service_class")
    })
    List<PluginServiceEntity> selectByPluginId(String pluginId);
    
    /**
     * 根据服务名称查询所有相关记录
     * 
     * @param serviceName 服务名称
     * @return 插件服务实体列表
     */
    @Select("SELECT id, plugin_id, service_name, service_class " +
            "FROM plugin_services WHERE service_name = #{serviceName}")
    @Results({
        @Result(property = "id", column = "id"),
        @Result(property = "pluginId", column = "plugin_id"),
        @Result(property = "serviceName", column = "service_name"),
        @Result(property = "serviceClass", column = "service_class")
    })
    List<PluginServiceEntity> selectByServiceName(String serviceName);
    
    /**
     * 查询所有插件服务记录
     * 
     * @return 插件服务实体列表
     */
    @Select("SELECT id, plugin_id, service_name, service_class " +
            "FROM plugin_services ORDER BY plugin_id, service_name")
    @Results({
        @Result(property = "id", column = "id"),
        @Result(property = "pluginId", column = "plugin_id"),
        @Result(property = "serviceName", column = "service_name"),
        @Result(property = "serviceClass", column = "service_class")
    })
    List<PluginServiceEntity> selectAll();
    
    /**
     * 检查是否存在特定的服务实现
     * 
     * @param pluginId 插件ID
     * @param serviceName 服务名称
     * @return 服务记录数量
     */
    @Select("SELECT COUNT(*) FROM plugin_services " +
            "WHERE plugin_id = #{pluginId} AND service_name = #{serviceName}")
    int countByPluginAndService(@Param("pluginId") String pluginId, @Param("serviceName") String serviceName);
    
    /**
     * 根据服务类查询服务记录
     * 
     * @param serviceClass 服务类名
     * @return 插件服务实体列表
     */
    @Select("SELECT id, plugin_id, service_name, service_class " +
            "FROM plugin_services WHERE service_class = #{serviceClass}")
    @Results({
        @Result(property = "id", column = "id"),
        @Result(property = "pluginId", column = "plugin_id"),
        @Result(property = "serviceName", column = "service_name"),
        @Result(property = "serviceClass", column = "service_class")
    })
    List<PluginServiceEntity> selectByServiceClass(String serviceClass);
    
    /**
     * 根据服务名称模糊查询
     * 
     * @param serviceName 服务名称（支持模糊匹配）
     * @return 插件服务实体列表
     */
    @Select("SELECT id, plugin_id, service_name, service_class " +
            "FROM plugin_services WHERE service_name LIKE CONCAT('%', #{serviceName}, '%') " +
            "ORDER BY plugin_id, service_name")
    @Results({
        @Result(property = "id", column = "id"),
        @Result(property = "pluginId", column = "plugin_id"),
        @Result(property = "serviceName", column = "service_name"),
        @Result(property = "serviceClass", column = "service_class")
    })
    List<PluginServiceEntity> selectByServiceNameLike(String serviceName);
}
