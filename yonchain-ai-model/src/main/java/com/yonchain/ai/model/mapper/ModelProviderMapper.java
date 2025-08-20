package com.yonchain.ai.model.mapper;

import com.yonchain.ai.model.entity.ModelProvider;
import com.yonchain.ai.model.handler.MapTypeHandler;
import com.yonchain.ai.model.handler.StringListTypeHandler;
import org.apache.ibatis.annotations.*;

import java.util.List;

/**
 * 模型提供商Mapper接口
 * 提供完整的增删改查操作
 */
@Mapper
public interface ModelProviderMapper {

    // ==================== 新增操作 ====================

    /**
     * 插入模型提供商
     * @param provider 模型提供商实体
     * @return 影响行数
     */
    @Insert("INSERT INTO ai_model_provider (code, name, description, provider_type, base_url, " +
            "api_key, config, config_schema, supported_model_types, enabled, sort_order, created_time, updated_time) " +
            "VALUES (#{code}, #{name}, #{description}, #{providerType}, #{baseUrl}, " +
            "#{apiKey}, #{config,typeHandler=com.yonchain.ai.model.handler.MapTypeHandler}, " +
            "#{configSchema,typeHandler=com.yonchain.ai.model.handler.MapTypeHandler}, " +
            "#{supportedModelTypes,typeHandler=com.yonchain.ai.model.handler.StringListTypeHandler}, " +
            "#{enabled}, #{sortOrder}, #{createdTime}, #{updatedTime})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(ModelProvider provider);

    /**
     * 批量插入模型提供商
     * @param providers 模型提供商列表
     * @return 影响行数
     */
    @Insert("<script>" +
            "INSERT INTO ai_model_provider (code, name, description, provider_type, base_url, " +
            "api_key, config, config_schema, supported_model_types, enabled, sort_order, created_time, updated_time) VALUES " +
            "<foreach collection='list' item='provider' separator=','>" +
            "(#{provider.code}, #{provider.name}, #{provider.description}, #{provider.providerType}, " +
            "#{provider.baseUrl}, #{provider.apiKey}, " +
            "#{provider.config,typeHandler=com.yonchain.ai.model.handler.MapTypeHandler}, " +
            "#{provider.configSchema,typeHandler=com.yonchain.ai.model.handler.MapTypeHandler}, " +
            "#{provider.supportedModelTypes,typeHandler=com.yonchain.ai.model.handler.StringListTypeHandler}, " +
            "#{provider.enabled}, #{provider.sortOrder}, #{provider.createdTime}, #{provider.updatedTime})" +
            "</foreach>" +
            "</script>")
    int insertBatch(@Param("list") List<ModelProvider> providers);

    // ==================== 删除操作 ====================

    /**
     * 根据ID删除模型提供商
     * @param id 提供商ID
     * @return 影响行数
     */
    @Delete("DELETE FROM ai_model_provider WHERE id = #{id}")
    int deleteById(@Param("id") Long id);

    /**
     * 根据提供商代码删除模型提供商
     * @param code 提供商代码
     * @return 影响行数
     */
    @Delete("DELETE FROM ai_model_provider WHERE code = #{code}")
    int deleteByCode(@Param("code") String code);

    /**
     * 根据提供商类型删除模型提供商
     * @param providerType 提供商类型
     * @return 影响行数
     */
    @Delete("DELETE FROM ai_model_provider WHERE provider_type = #{providerType}")
    int deleteByProviderType(@Param("providerType") String providerType);

    /**
     * 批量删除模型提供商
     * @param ids 提供商ID列表
     * @return 影响行数
     */
    @Delete("<script>" +
            "DELETE FROM ai_model_provider WHERE id IN " +
            "<foreach collection='ids' item='id' open='(' separator=',' close=')'>" +
            "#{id}" +
            "</foreach>" +
            "</script>")
    int deleteBatchByIds(@Param("ids") List<Long> ids);

    // ==================== 修改操作 ====================

    /**
     * 更新模型提供商
     * @param provider 模型提供商实体
     * @return 影响行数
     */
    @Update("UPDATE ai_model_provider SET " +
            "name = #{name}, " +
            "description = #{description}, " +
            "provider_type = #{providerType}, " +
            "base_url = #{baseUrl}, " +
            "api_key = #{apiKey}, " +
            "config = #{config,typeHandler=com.yonchain.ai.model.handler.MapTypeHandler}, " +
            "config_schema = #{configSchema,typeHandler=com.yonchain.ai.model.handler.MapTypeHandler}, " +
            "supported_model_types = #{supportedModelTypes,typeHandler=com.yonchain.ai.model.handler.StringListTypeHandler}, " +
            "enabled = #{enabled}, " +
            "sort_order = #{sortOrder}, " +
            "updated_time = #{updatedTime} " +
            "WHERE id = #{id}")
    int updateById(ModelProvider provider);

    /**
     * 根据提供商代码更新模型提供商
     * @param provider 模型提供商实体
     * @return 影响行数
     */
    @Update("UPDATE ai_model_provider SET " +
            "name = #{name}, " +
            "description = #{description}, " +
            "provider_type = #{providerType}, " +
            "base_url = #{baseUrl}, " +
            "api_key = #{apiKey}, " +
            "config = #{config}, " +
            "enabled = #{enabled}, " +
            "sort_order = #{sortOrder}, " +
            "updated_time = #{updatedTime} " +
            "WHERE code = #{code}")
    int updateByCode(ModelProvider provider);

    /**
     * 选择性更新模型提供商（只更新非空字段）
     * @param provider 模型提供商实体
     * @return 影响行数
     */
    @Update("<script>" +
            "UPDATE ai_model_provider " +
            "<set>" +
            "<if test='name != null and name != \"\"'>name = #{name},</if>" +
            "<if test='description != null'>description = #{description},</if>" +
            "<if test='providerType != null and providerType != \"\"'>provider_type = #{providerType},</if>" +
            "<if test='baseUrl != null and baseUrl != \"\"'>base_url = #{baseUrl},</if>" +
            "<if test='apiKey != null and apiKey != \"\"'>api_key = #{apiKey},</if>" +
            "<if test='config != null'>config = #{config},</if>" +
            "<if test='enabled != null'>enabled = #{enabled},</if>" +
            "<if test='sortOrder != null'>sort_order = #{sortOrder},</if>" +
            "<if test='updatedTime != null'>updated_time = #{updatedTime},</if>" +
            "</set>" +
            "WHERE id = #{id}" +
            "</script>")
    int updateSelectiveById(ModelProvider provider);

    /**
     * 批量更新提供商状态
     * @param ids 提供商ID列表
     * @param enabled 启用状态
     * @return 影响行数
     */
    @Update("<script>" +
            "UPDATE ai_model_provider SET enabled = #{enabled}, updated_time = NOW() WHERE id IN " +
            "<foreach collection='ids' item='id' open='(' separator=',' close=')'>" +
            "#{id}" +
            "</foreach>" +
            "</script>")
    int updateEnabledBatchByIds(@Param("ids") List<Long> ids, @Param("enabled") Boolean enabled);

    /**
     * 更新API密钥
     * @param id 提供商ID
     * @param apiKey API密钥
     * @return 影响行数
     */
    @Update("UPDATE ai_model_provider SET api_key = #{apiKey}, updated_time = NOW() WHERE id = #{id}")
    int updateApiKeyById(@Param("id") Long id, @Param("apiKey") String apiKey);

    /**
     * 更新基础URL
     * @param id 提供商ID
     * @param baseUrl 基础URL
     * @return 影响行数
     */
    @Update("UPDATE ai_model_provider SET base_url = #{baseUrl}, updated_time = NOW() WHERE id = #{id}")
    int updateBaseUrlById(@Param("id") Long id, @Param("baseUrl") String baseUrl);

    // ==================== 查询操作 ====================

    /**
     * 根据ID查询模型提供商
     * @param id 提供商ID
     * @return 模型提供商实体
     */
    @Select("SELECT * FROM ai_model_provider WHERE id = #{id}")
    ModelProvider selectById(@Param("id") Long id);

    /**
     * 根据提供商代码查询提供商信息
     * @param code 提供商代码
     * @return 提供商信息
     */
    @Select("SELECT * FROM ai_model_provider WHERE code = #{code}")
    ModelProvider selectByCode(@Param("code") String code);

    /**
     * 根据提供商类型查询提供商列表
     * @param providerType 提供商类型
     * @return 提供商列表
     */
    @Select("SELECT * FROM ai_model_provider WHERE provider_type = #{providerType} ORDER BY sort_order ASC, id ASC")
    List<ModelProvider> selectByProviderType(@Param("providerType") String providerType);

    /**
     * 查询所有模型提供商
     * @return 提供商列表
     */
    @Select("SELECT * FROM ai_model_provider ORDER BY sort_order ASC, id ASC")
    List<ModelProvider> selectAll();

    /**
     * 查询启用的模型提供商
     * @return 提供商列表
     */
    @Select("SELECT * FROM ai_model_provider WHERE enabled = true ORDER BY sort_order ASC, id ASC")
    List<ModelProvider> selectEnabled();

    /**
     * 分页查询模型提供商
     * @param offset 偏移量
     * @param limit 限制数量
     * @return 提供商列表
     */
    @Select("SELECT * FROM ai_model_provider ORDER BY sort_order ASC, id ASC LIMIT #{offset}, #{limit}")
    List<ModelProvider> selectByPage(@Param("offset") int offset, @Param("limit") int limit);

    /**
     * 根据条件查询模型提供商
     * @param provider 查询条件
     * @return 提供商列表
     */
    @Select("<script>" +
            "SELECT * FROM ai_model_provider " +
            "<where>" +
            "<if test='code != null and code != \"\"'>AND code = #{code}</if>" +
            "<if test='name != null and name != \"\"'>AND name LIKE CONCAT('%', #{name}, '%')</if>" +
            "<if test='providerType != null and providerType != \"\"'>AND provider_type = #{providerType}</if>" +
            "<if test='enabled != null'>AND enabled = #{enabled}</if>" +
            "</where>" +
            "ORDER BY sort_order ASC, id ASC" +
            "</script>")
    List<ModelProvider> selectByCondition(ModelProvider provider);

    /**
     * 根据ID列表查询模型提供商
     * @param ids 提供商ID列表
     * @return 提供商列表
     */
    @Select("<script>" +
            "SELECT * FROM ai_model_provider WHERE id IN " +
            "<foreach collection='ids' item='id' open='(' separator=',' close=')'>" +
            "#{id}" +
            "</foreach>" +
            "ORDER BY sort_order ASC, id ASC" +
            "</script>")
    List<ModelProvider> selectByIds(@Param("ids") List<Long> ids);

    /**
     * 根据名称模糊查询模型提供商
     * @param name 提供商名称
     * @return 提供商列表
     */
    @Select("SELECT * FROM ai_model_provider WHERE name LIKE CONCAT('%', #{name}, '%') ORDER BY sort_order ASC, id ASC")
    List<ModelProvider> selectByNameLike(@Param("name") String name);

    // ==================== 统计操作 ====================

    /**
     * 统计模型提供商总数
     * @return 总数
     */
    @Select("SELECT COUNT(*) FROM ai_model_provider")
    long countAll();

    /**
     * 统计启用的模型提供商数量
     * @return 启用数量
     */
    @Select("SELECT COUNT(*) FROM ai_model_provider WHERE enabled = true")
    long countEnabled();

    /**
     * 根据提供商类型统计数量
     * @param providerType 提供商类型
     * @return 数量
     */
    @Select("SELECT COUNT(*) FROM ai_model_provider WHERE provider_type = #{providerType}")
    long countByProviderType(@Param("providerType") String providerType);

    /**
     * 根据条件统计模型提供商数量
     * @param provider 查询条件
     * @return 数量
     */
    @Select("<script>" +
            "SELECT COUNT(*) FROM ai_model_provider " +
            "<where>" +
            "<if test='code != null and code != \"\"'>AND code = #{code}</if>" +
            "<if test='name != null and name != \"\"'>AND name LIKE CONCAT('%', #{name}, '%')</if>" +
            "<if test='providerType != null and providerType != \"\"'>AND provider_type = #{providerType}</if>" +
            "<if test='enabled != null'>AND enabled = #{enabled}</if>" +
            "</where>" +
            "</script>")
    long countByCondition(ModelProvider provider);

    // ==================== 存在性检查 ====================

    /**
     * 检查提供商代码是否存在
     * @param code 提供商代码
     * @return 是否存在
     */
    @Select("SELECT COUNT(*) > 0 FROM ai_model_provider WHERE code = #{code}")
    boolean existsByCode(@Param("code") String code);

    /**
     * 检查提供商代码是否存在（排除指定ID）
     * @param code 提供商代码
     * @param excludeId 排除的ID
     * @return 是否存在
     */
    @Select("SELECT COUNT(*) > 0 FROM ai_model_provider WHERE code = #{code} AND id != #{excludeId}")
    boolean existsByCodeExcludeId(@Param("code") String code, @Param("excludeId") Long excludeId);

    /**
     * 检查提供商名称是否存在
     * @param name 提供商名称
     * @return 是否存在
     */
    @Select("SELECT COUNT(*) > 0 FROM ai_model_provider WHERE name = #{name}")
    boolean existsByName(@Param("name") String name);

    /**
     * 检查提供商名称是否存在（排除指定ID）
     * @param name 提供商名称
     * @param excludeId 排除的ID
     * @return 是否存在
     */
    @Select("SELECT COUNT(*) > 0 FROM ai_model_provider WHERE name = #{name} AND id != #{excludeId}")
    boolean existsByNameExcludeId(@Param("name") String name, @Param("excludeId") Long excludeId);
}
