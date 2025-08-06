package com.yonchain.ai.console.sys.mapper;

import com.yonchain.ai.console.sys.entity.LoginLogEntity;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface LoginLogMapper {

    /**
     * 插入登录日志
     * @param loginLog 登录日志实体
     * @return 影响行数
     */
    int insert(LoginLogEntity loginLog);

    /**
     * 根据用户ID查询登录日志
     * @param userId 用户ID
     * @return 登录日志列表
     */
    List<LoginLogEntity> selectByUserId(String userId);

    /**
     * 查询最近登录日志
     * @param limit 查询条数
     * @return 登录日志列表
     */
    List<LoginLogEntity> selectRecentLogs(int limit);
}
