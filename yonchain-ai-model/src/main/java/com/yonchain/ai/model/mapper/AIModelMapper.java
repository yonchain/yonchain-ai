package com.yonchain.ai.model.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.yonchain.ai.model.entity.AIModel;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * AI模型Mapper接口
 */
@Mapper
public interface AIModelMapper extends BaseMapper<AIModel> {

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
    @Select("SELECT * FROM ai_model WHERE provider_id = #{providerId}")
    List<AIModel> selectByProviderId(@Param("providerId") Long providerId);

    /**
     * 根据模型类型查询模型列表
     * @param modelType 模型类型
     * @return 模型列表
     */
    @Select("SELECT * FROM ai_model WHERE model_type = #{modelType} AND enabled = true")
    List<AIModel> selectByModelType(@Param("modelType") String modelType);

    /**
     * 根据提供商ID删除模型
     * @param providerId 提供商ID
     * @return 影响行数
     */
    @Delete("DELETE FROM ai_model WHERE provider_id = #{providerId}")
    int deleteByProviderId(@Param("providerId") Long providerId);
}