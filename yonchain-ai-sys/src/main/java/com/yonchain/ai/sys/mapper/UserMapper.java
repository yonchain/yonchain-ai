package com.yonchain.ai.sys.mapper;

import com.yonchain.ai.api.sys.User;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * 用户数据访问接口
 *
 * @author Cgy
 * @since 1.0.0
 */
public interface UserMapper {

    /**
     * 通过ID查询用户
     *
     * @param id 主键ID
     * @return 用户实体
     */
    User selectById(@Param("id") String id);

    /**
     * 通过用户名查询用户
     *
     * @param userName 用户名
     * @return 用户实体
     */
    User selectByUsername(@Param("userName") String userName);

    /**
     * 通过邮箱查询用户
     *
     * @param email 邮箱
     * @return 用户实体
     */
    User selectByEmail(@Param("email") String email);

    /**
     * 根据条件查询用户列表
     *
     * @param tenantId 租户id
     * @param params   查询参数
     * @return 用户列表
     */
    List<User> selectList(@Param("tenantId") String tenantId, @Param("params") Map<String, Object> params);

    /**
     * 新增用户
     *
     * @param user 用户实体
     * @return 影响行数
     */
    int insert(User user);

    /**
     * 修改用户
     *
     * @param user 用户实体
     * @return 影响行数
     */
    int updateById(User user);

    /**
     * 通过ID删除用户
     *
     * @param id 主键ID
     * @return 影响行数
     */
    int deleteById(@Param("id") String id);

    /**
     * 批量删除用户
     *
     * @param ids ID列表
     * @return 影响行数
     */
    int deleteByIds(@Param("ids") List<String> ids);

    /**
     * 更新最后登录信息
     *
     * @param id      用户ID
     * @param loginIp 登录IP
     * @return 影响行数
     */
    int updateLastLogin(@Param("id") String id, @Param("loginIp") String loginIp);

}
