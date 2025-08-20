package com.yonchain.ai.model.mapper;

import com.yonchain.ai.model.entity.AIModel;
import com.yonchain.ai.model.handler.MapTypeHandler;
import com.yonchain.ai.model.handler.StringArrayTypeHandler;
import org.apache.ibatis.annotations.*;

import java.util.List;

/**
 * AI模型Mapper接口
 * 提供完整的增删改查操作
 */
@Mapper
public interface AIModelMapper {

    // ==================== 新增操作 ====================

    /**
     * 插入AI模型
     * @param model AI模型实体
     * @return 影响行数
     */
    @Insert("INSERT INTO ai_model (code, name, description, model_type, provider_id, provider_code, " +
            "capabilities, config, config_schema, enabled, sort_order, created_time, updated_time) " +
            "VALUES (#{code}, #{name}, #{description}, #{modelType}, #{providerId}, #{providerCode}, " +
            "#{capabilities,typeHandler=com.yonchain.ai.model.handler.StringArrayTypeHandler}, " +
            "#{config,typeHandler=com.yonchain.ai.model.handler.MapTypeHandler}, " +
            "#{configSchema,typeHandler=com.yonchain.ai.model.handler.MapTypeHandler}, " +
            "#{enabled}, #{sortOrder}, #{createdTime}, #{updatedTime})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(AIModel model);

    /**
     * 批量插入AI模型
     * @param models AI模型列表
     * @return 影响行数
     */
    @Insert("<script>" +
            "INSERT INTO ai_model (code, name, description, model_type, provider_id, provider_code, " +
            "capabilities, config, config_schema, enabled, sort_order, created_time, updated_time) VALUES " +
            "<foreach collection='list' item='model' separator=','>" +
            "(#{model.code}, #{model.name}, #{model.description}, #{model.modelType}, #{model.providerId}, " +
            "#{model.providerCode}, #{model.capabilities,typeHandler=com.yonchain.ai.model.handler.StringArrayTypeHandler}, " +
            "#{model.config,typeHandler=com.yonchain.ai.model.handler.MapTypeHandler}, " +
            "#{model.configSchema,typeHandler=com.yonchain.ai.model.handler.MapTypeHandler}, " +
            "#{model.enabled}, #{model.sortOrder}, #{model.createdTime}, #{model.updatedTime})" +
            "</foreach>" +
            "</script>")
    int insertBatch(@Param("list") List<AIModel> models);

    // ==================== 删除操作 ====================

    /**
     * 根据ID删除AI模型
     * @param id 模型ID
     * @return 影响行数
     */
    @Delete("DELETE FROM ai_model WHERE id = #{id}")
    int deleteById(@Param("id") Long id);

    /**
     * 根据模型代码删除AI模型
     * @param code 模型代码
     * @return 影响行数
     */
    @Delete("DELETE FROM ai_model WHERE code = #{code}")
    int deleteByCode(@Param("code") String code);

    /**
     * 根据提供商ID删除模型
     * @param providerId 提供商ID
     * @return 影响行数
     */
    @Delete("DELETE FROM ai_model WHERE provider_id = #{providerId}")
    int deleteByProviderId(@Param("providerId") Long providerId);

    /**
     * 根据提供商代码删除模型
     * @param providerCode 提供商代码
     * @return 影响行数
     */
    @Delete("DELETE FROM ai_model WHERE provider_code = #{providerCode}")
    int deleteByProviderCode(@Param("providerCode") String providerCode);

    /**
     * 批量删除AI模型
     * @param ids 模型ID列表
     * @return 影响行数
     */
    @Delete("<script>" +
            "DELETE FROM ai_model WHERE id IN " +
            "<foreach collection='ids' item='id' open='(' separator=',' close=')'>" +
            "#{id}" +
            "</foreach>" +
            "</script>")
    int deleteBatchByIds(@Param("ids") List<Long> ids);

    // ==================== 修改操作 ====================

    /**
     * 更新AI模型
     * @param model AI模型实体
     * @return 影响行数
     */
    @Update("UPDATE ai_model SET " +
            "name = #{name}, " +
            "description = #{description}, " +
            "model_type = #{modelType}, " +
            "provider_id = #{providerId}, " +
            "provider_code = #{providerCode}, " +
            "capabilities = #{capabilities,typeHandler=com.yonchain.ai.model.handler.StringArrayTypeHandler}, " +
            "config = #{config,typeHandler=com.yonchain.ai.model.handler.MapTypeHandler}, " +
            "config_schema = #{configSchema,typeHandler=com.yonchain.ai.model.handler.MapTypeHandler}, " +
            "enabled = #{enabled}, " +
            "sort_order = #{sortOrder}, " +
            "updated_time = #{updatedTime} " +
            "WHERE id = #{id}")
    int updateById(AIModel model);

    /**
     * 根据模型代码更新AI模型
     * @param model AI模型实体
     * @return 影响行数
     */
    @Update("UPDATE ai_model SET " +
            "name = #{name}, " +
            "description = #{description}, " +
            "model_type = #{modelType}, " +
            "provider_id = #{providerId}, " +
            "provider_code = #{providerCode}, " +
            "capabilities = #{capabilities,typeHandler=com.yonchain.ai.model.handler.StringArrayTypeHandler}, " +
            "config = #{config,typeHandler=com.yonchain.ai.model.handler.MapTypeHandler}, " +
            "config_schema = #{configSchema,typeHandler=com.yonchain.ai.model.handler.MapTypeHandler}, " +
            "enabled = #{enabled}, " +
            "sort_order = #{sortOrder}, " +
            "updated_time = #{updatedTime} " +
            "WHERE code = #{code}")
    int updateByCode(AIModel model);

    /**
     * 选择性更新AI模型（只更新非空字段）
     * @param model AI模型实体
     * @return 影响行数
     */
    @Update("<script>" +
            "UPDATE ai_model " +
            "<set>" +
            "<if test='name != null and name != \"\"'>name = #{name},</if>" +
            "<if test='description != null'>description = #{description},</if>" +
            "<if test='modelType != null and modelType != \"\"'>model_type = #{modelType},</if>" +
            "<if test='providerId != null'>provider_id = #{providerId},</if>" +
            "<if test='providerCode != null and providerCode != \"\"'>provider_code = #{providerCode},</if>" +
            "<if test='capabilities != null'>capabilities = #{capabilities,typeHandler=com.yonchain.ai.model.handler.StringArrayTypeHandler},</if>" +
            "<if test='config != null'>config = #{config,typeHandler=com.yonchain.ai.model.handler.MapTypeHandler},</if>" +
            "<if test='configSchema != null'>config_schema = #{configSchema,typeHandler=com.yonchain.ai.model.handler.MapTypeHandler},</if>" +
            "<if test='enabled != null'>enabled = #{enabled},</if>" +
            "<if test='sortOrder != null'>sort_order = #{sortOrder},</if>" +
            "<if test='updatedTime != null'>updated_time = #{updatedTime},</if>" +
            "</set>" +
            "WHERE id = #{id}" +
            "</script>")
    int updateSelectiveById(AIModel model);

    /**
     * 批量更新模型状态
     * @param ids 模型ID列表
     * @param enabled 启用状态
     * @return 影响行数
     */
    @Update("<script>" +
            "UPDATE ai_model SET enabled = #{enabled}, updated_time = NOW() WHERE id IN " +
            "<foreach collection='ids' item='id' open='(' separator=',' close=')'>" +
            "#{id}" +
            "</foreach>" +
            "</script>")
    int updateEnabledBatchByIds(@Param("ids") List<Long> ids, @Param("enabled") Boolean enabled);

    // ==================== 查询操作 ====================

    /**
     * 根据ID查询AI模型
     * @param id 模型ID
     * @return AI模型实体
     */
    @Select("SELECT * FROM ai_model WHERE id = #{id}")
    AIModel selectById(@Param("id") Long id);

    /**
     * 根据模型代码查询模型信息
     * @param code 模型代码
     * @return 模型信息
     */
    @Select("SELECT * FROM ai_model WHERE code = #{code}")
    AIModel selectByCode(@Param("code") String code);

    /**
     * 根据提供商ID查询模型列表
     * @param providerId 提供商ID
     * @return 模型列表
     */
    @Select("SELECT * FROM ai_model WHERE provider_id = #{providerId} ORDER BY sort_order ASC, id ASC")
    List<AIModel> selectByProviderId(@Param("providerId") Long providerId);

    /**
     * 根据提供商代码查询模型列表
     * @param providerCode 提供商代码
     * @return 模型列表
     */
    @Select("SELECT * FROM ai_model WHERE provider_code = #{providerCode} ORDER BY sort_order ASC, id ASC")
    List<AIModel> selectByProviderCode(@Param("providerCode") String providerCode);

    /**
     * 根据模型类型查询模型列表
     * @param modelType 模型类型
     * @return 模型列表
     */
    @Select("SELECT * FROM ai_model WHERE model_type = #{modelType} AND enabled = true ORDER BY sort_order ASC, id ASC")
    List<AIModel> selectByModelType(@Param("modelType") String modelType);

    /**
     * 查询所有AI模型
     * @return 模型列表
     */
    @Select("SELECT * FROM ai_model ORDER BY sort_order ASC, id ASC")
    List<AIModel> selectAll();

    /**
     * 查询启用的AI模型
     * @return 模型列表
     */
    @Select("SELECT * FROM ai_model WHERE enabled = true ORDER BY sort_order ASC, id ASC")
    List<AIModel> selectEnabled();

    /**
     * 分页查询AI模型
     * @param offset 偏移量
     * @param limit 限制数量
     * @return 模型列表
     */
    @Select("SELECT * FROM ai_model ORDER BY sort_order ASC, id ASC LIMIT #{offset}, #{limit}")
    List<AIModel> selectByPage(@Param("offset") int offset, @Param("limit") int limit);

    /**
     * 根据条件查询AI模型
     * @param model 查询条件
     * @return 模型列表
     */
    @Select("<script>" +
            "SELECT * FROM ai_model " +
            "<where>" +
            "<if test='code != null and code != \"\"'>AND code = #{code}</if>" +
            "<if test='name != null and name != \"\"'>AND name LIKE CONCAT('%', #{name}, '%')</if>" +
            "<if test='modelType != null and modelType != \"\"'>AND model_type = #{modelType}</if>" +
            "<if test='providerId != null'>AND provider_id = #{providerId}</if>" +
            "<if test='providerCode != null and providerCode != \"\"'>AND provider_code = #{providerCode}</if>" +
            "<if test='enabled != null'>AND enabled = #{enabled}</if>" +
            "</where>" +
            "ORDER BY sort_order ASC, id ASC" +
            "</script>")
    List<AIModel> selectByCondition(AIModel model);

    /**
     * 根据ID列表查询AI模型
     * @param ids 模型ID列表
     * @return 模型列表
     */
    @Select("<script>" +
            "SELECT * FROM ai_model WHERE id IN " +
            "<foreach collection='ids' item='id' open='(' separator=',' close=')'>" +
            "#{id}" +
            "</foreach>" +
            "ORDER BY sort_order ASC, id ASC" +
            "</script>")
    List<AIModel> selectByIds(@Param("ids") List<Long> ids);

    // ==================== 统计操作 ====================

    /**
     * 统计AI模型总数
     * @return 总数
     */
    @Select("SELECT COUNT(*) FROM ai_model")
    long countAll();

    /**
     * 统计启用的AI模型数量
     * @return 启用数量
     */
    @Select("SELECT COUNT(*) FROM ai_model WHERE enabled = true")
    long countEnabled();

    /**
     * 根据提供商ID统计模型数量
     * @param providerId 提供商ID
     * @return 模型数量
     */
    @Select("SELECT COUNT(*) FROM ai_model WHERE provider_id = #{providerId}")
    long countByProviderId(@Param("providerId") Long providerId);

    /**
     * 根据模型类型统计数量
     * @param modelType 模型类型
     * @return 数量
     */
    @Select("SELECT COUNT(*) FROM ai_model WHERE model_type = #{modelType}")
    long countByModelType(@Param("modelType") String modelType);

    /**
     * 根据条件统计AI模型数量
     * @param model 查询条件
     * @return 数量
     */
    @Select("<script>" +
            "SELECT COUNT(*) FROM ai_model " +
            "<where>" +
            "<if test='code != null and code != \"\"'>AND code = #{code}</if>" +
            "<if test='name != null and name != \"\"'>AND name LIKE CONCAT('%', #{name}, '%')</if>" +
            "<if test='modelType != null and modelType != \"\"'>AND model_type = #{modelType}</if>" +
            "<if test='providerId != null'>AND provider_id = #{providerId}</if>" +
            "<if test='providerCode != null and providerCode != \"\"'>AND provider_code = #{providerCode}</if>" +
            "<if test='enabled != null'>AND enabled = #{enabled}</if>" +
            "</where>" +
            "</script>")
    long countByCondition(AIModel model);

    // ==================== 存在性检查 ====================

    /**
     * 检查模型代码是否存在
     * @param code 模型代码
     * @return 是否存在
     */
    @Select("SELECT COUNT(*) > 0 FROM ai_model WHERE code = #{code}")
    boolean existsByCode(@Param("code") String code);

    /**
     * 检查模型代码是否存在（排除指定ID）
     * @param code 模型代码
     * @param excludeId 排除的ID
     * @return 是否存在
     */
    @Select("SELECT COUNT(*) > 0 FROM ai_model WHERE code = #{code} AND id != #{excludeId}")
    boolean existsByCodeExcludeId(@Param("code") String code, @Param("excludeId") Long excludeId);
}
