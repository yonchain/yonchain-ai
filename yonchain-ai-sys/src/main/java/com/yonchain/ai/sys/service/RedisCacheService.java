package com.yonchain.ai.sys.service;

import com.yonchain.ai.api.sys.*;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * Redis缓存服务实现类
 * 使用Redis作为缓存存储，实现IdmCacheService接口
 */
@Service
public class RedisCacheService implements IdmCacheService {

    // 用户缓存key前缀
    private static final String USER_CACHE_PREFIX = "user:";
    // 租户缓存key前缀
    private static final String TENANT_CACHE_PREFIX = "tenant:";
    // 角色缓存key前缀
    private static final String ROLES_CACHE_PREFIX = "roles:";
    // 缓存过期时间(小时)
    private static final long CACHE_EXPIRE_HOURS = 1;

    private final RedisTemplate<String, Object> redisTemplate; // Redis操作模板


    /**
     * 构造方法
     *
     * @param redisTemplate Redis操作模板
     */
    public RedisCacheService(RedisTemplate<String, Object> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    /**
     * 根据用户ID获取用户信息
     * 先从Redis缓存中获取，如果不存在则从用户服务获取并缓存
     *
     * @param userId 用户ID
     * @return 用户对象
     */
    public User getUserById(String userId) {
        String key = USER_CACHE_PREFIX + userId;
        return (User) redisTemplate.opsForValue().get(key);
    }

    /**
     * 缓存用户信息
     *
     * @param user 要缓存的用户对象，如果为null则不进行缓存
     */
    public void cacheUser(User user) {
        if (user == null) {
            return;
        }
        String key = USER_CACHE_PREFIX + user.getId();
        redisTemplate.opsForValue().set(key, user, CACHE_EXPIRE_HOURS, TimeUnit.HOURS);
    }

    /**
     * 根据用户ID获取租户信息
     * 先从Redis缓存中获取，如果不存在则从租户服务获取并缓存
     *
     * @param userId 用户ID
     * @return 租户对象
     */
    public Tenant getTenantByUserId(String userId) {
        String key = TENANT_CACHE_PREFIX + userId;
        return (Tenant) redisTemplate.opsForValue().get(key);
    }

    /**
     * 缓存租户信息
     *
     * @param userId 用户ID，用于构建缓存key
     * @param tenant 要缓存的租户对象，如果为null则不进行缓存
     */
    public void cacheTenant(String userId, Tenant tenant) {
        if (tenant == null) {
            return;
        }
        String key = TENANT_CACHE_PREFIX + userId;
        redisTemplate.opsForValue().set(key, tenant, CACHE_EXPIRE_HOURS, TimeUnit.HOURS);
    }

    /**
     * 获取用户角色列表
     * 先从Redis缓存中获取，如果不存在则从用户服务获取并缓存
     *
     * @param tenantId 租户ID
     * @param userId   用户ID
     * @return 角色列表
     */
    public List<Role> getUserRoles(String tenantId, String userId) {
        String key = ROLES_CACHE_PREFIX + tenantId + ":" + userId;
        return (List<Role>) redisTemplate.opsForValue().get(key);
    }

    /**
     * 缓存用户角色列表
     *
     * @param tenantId 租户ID，用于构建缓存key
     * @param userId   用户ID，用于构建缓存key
     * @param roles    要缓存的角色列表
     */
    public void cacheUserRoles(String tenantId, String userId, List<Role> roles) {
        String key = ROLES_CACHE_PREFIX + tenantId + ":" + userId;
        redisTemplate.opsForValue().set(key, roles, CACHE_EXPIRE_HOURS, TimeUnit.HOURS);
    }

    /**
     * 清除用户缓存
     *
     * @param userId 用户ID，用于定位要清除的缓存
     */
    public void clearUserCache(String userId) {
        redisTemplate.delete(USER_CACHE_PREFIX + userId);
    }

    /**
     * 清除租户缓存
     *
     * @param userId 用户ID，用于定位要清除的租户缓存
     */
    public void clearTenantCache(String userId) {
        redisTemplate.delete(TENANT_CACHE_PREFIX + userId);
    }

    /**
     * 清除角色缓存
     *
     * @param tenantId 租户ID，用于定位要清除的角色缓存
     * @param userId   用户ID，用于定位要清除的角色缓存
     */
    public void clearRolesCache(String tenantId, String userId) {
        redisTemplate.delete(ROLES_CACHE_PREFIX + tenantId + ":" + userId);
    }
}
