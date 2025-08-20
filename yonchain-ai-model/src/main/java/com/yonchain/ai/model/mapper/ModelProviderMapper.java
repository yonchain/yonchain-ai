package com.yonchain.ai.model.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.yonchain.ai.model.entity.ModelProvider;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

/**
 * 模型提供商Mapper接口
 */
@Mapper
public interface ModelProviderMapper extends BaseMapper<ModelProvider> {

    /**
     * 根据提供商代码查询提供商信息
     * @param code 提供商代码
     * @return 提供商信息
     */
    @Select("SELECT * FROM ai_model_provider WHERE code = #{code}")
    ModelProvider selectByCode(@Param("code") String code);
}