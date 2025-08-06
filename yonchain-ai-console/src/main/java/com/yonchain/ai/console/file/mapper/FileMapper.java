package com.yonchain.ai.console.file.mapper;

import com.yonchain.ai.console.file.entity.FileEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * 文件Mapper接口
 *
 * @author Cgy
 * @since 1.0.0
 */
@Mapper
public interface FileMapper {

    FileEntity selectById(@Param("id") String id);

    long countByTenantIdAndParams(@Param("tenantId") String tenantId,
                                 @Param("params") Map<String, Object> params);

    List<FileEntity> selectPageByTenantIdAndParams(@Param("tenantId") String tenantId,
                                           @Param("params") Map<String, Object> params,
                                           @Param("offset") int offset,
                                           @Param("limit") int limit);

    int insert(FileEntity file);

    int updateById(FileEntity file);

    int deleteById(@Param("id") String id);

    int deleteBatchIds(@Param("ids") List<String> ids);
}
