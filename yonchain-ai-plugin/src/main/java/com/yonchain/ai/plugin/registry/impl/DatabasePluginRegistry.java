package com.yonchain.ai.plugin.registry.impl;

import com.yonchain.ai.plugin.Plugin;
import com.yonchain.ai.plugin.entity.PluginEntity;
import com.yonchain.ai.plugin.entity.PluginInfo;
import com.yonchain.ai.plugin.enums.PluginStatus;
import com.yonchain.ai.plugin.enums.PluginType;
import com.yonchain.ai.plugin.mapper.PluginDependencyMapper;
import com.yonchain.ai.plugin.mapper.PluginExtensionMapper;
import com.yonchain.ai.plugin.mapper.PluginMapper;
import com.yonchain.ai.plugin.mapper.PluginServiceMapper;
import com.yonchain.ai.plugin.registry.PluginRegistry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * 基于数据库的插件注册表实现
 * 
 * @author yonchain
 */
@Component
public class DatabasePluginRegistry implements PluginRegistry {
    
    private static final Logger log = LoggerFactory.getLogger(DatabasePluginRegistry.class);
    
    // 内存缓存运行时插件实例
    private final Map<String, Plugin> pluginInstances = new ConcurrentHashMap<>();
    
    @Autowired
    private PluginMapper pluginMapper;
    
    @Autowired
    private PluginDependencyMapper dependencyMapper;
    
    @Autowired
    private PluginExtensionMapper extensionMapper;
    
    @Autowired
    private PluginServiceMapper serviceMapper;
    
    @Override
    @Transactional
    public PluginInfo save(PluginInfo pluginInfo) {
        if (pluginInfo == null) {
            throw new IllegalArgumentException("PluginInfo cannot be null");
        }
        
        String pluginId = pluginInfo.getPluginId();
        log.info("Saving plugin info: {}", pluginId);
        
        try {
            // 转换为数据库实体
            PluginEntity entity = convertToEntity(pluginInfo);
            
            // 检查是否已存在
            if (pluginMapper.existsById(pluginId)) {
                // 更新现有插件
                entity.setUpdatedAt(LocalDateTime.now());
                pluginMapper.update(entity);
                log.info("Updated existing plugin: {}", pluginId);
            } else {
                // 插入新插件
                entity.setCreatedAt(LocalDateTime.now());
                entity.setUpdatedAt(LocalDateTime.now());
                pluginMapper.insert(entity);
                log.info("Inserted new plugin: {}", pluginId);
            }
            
            return pluginInfo;
            
        } catch (Exception e) {
            log.error("Failed to save plugin info: {}", pluginId, e);
            throw new RuntimeException("Failed to save plugin info: " + pluginId, e);
        }
    }
    
    @Override
    public Optional<PluginInfo> findByPluginId(String pluginId) {
        if (pluginId == null || pluginId.trim().isEmpty()) {
            return Optional.empty();
        }
        
        PluginEntity entity = pluginMapper.findById(pluginId);
        return entity != null ? Optional.of(convertToPluginInfo(entity)) : Optional.empty();
    }
    
    @Override
    public List<PluginInfo> findByName(String name) {
        if (name == null || name.trim().isEmpty()) {
            return new ArrayList<>();
        }
        
        return pluginMapper.findAll().stream()
                .filter(entity -> name.equals(entity.getName()))
                .map(this::convertToPluginInfo)
                .collect(Collectors.toList());
    }
    
    @Override
    public List<PluginInfo> findByType(PluginType type) {
        if (type == null) {
            return new ArrayList<>();
        }
        
        return pluginMapper.findByType(type).stream()
                .map(this::convertToPluginInfo)
                .collect(Collectors.toList());
    }
    
    @Override
    public List<PluginInfo> findByStatus(PluginStatus status) {
        if (status == null) {
            return new ArrayList<>();
        }
        
        return pluginMapper.findByStatus(status).stream()
                .map(this::convertToPluginInfo)
                .collect(Collectors.toList());
    }
    
    @Override
    public List<PluginInfo> findByEnabled(boolean enabled) {
        PluginStatus targetStatus = enabled ? PluginStatus.INSTALLED_ENABLED : PluginStatus.INSTALLED_DISABLED;
        return findByStatus(targetStatus);
    }
    
    @Override
    public List<PluginInfo> findByTypeAndStatus(PluginType type, PluginStatus status) {
        if (type == null || status == null) {
            return new ArrayList<>();
        }
        
        return pluginMapper.findAll().stream()
                .filter(entity -> type.equals(entity.getType()) && status.equals(entity.getStatus()))
                .map(this::convertToPluginInfo)
                .collect(Collectors.toList());
    }
    
    @Override
    public List<PluginInfo> findAll() {
        return pluginMapper.findAll().stream()
                .map(this::convertToPluginInfo)
                .collect(Collectors.toList());
    }
    
    @Override
    public boolean existsByPluginId(String pluginId) {
        if (pluginId == null || pluginId.trim().isEmpty()) {
            return false;
        }
        return pluginMapper.existsById(pluginId);
    }
    
    @Override
    @Transactional
    public void deleteByPluginId(String pluginId) {
        if (pluginId == null || pluginId.trim().isEmpty()) {
            return;
        }
        
        log.info("Deleting plugin: {}", pluginId);
        
        try {
            // 删除关联数据
            dependencyMapper.deleteByPluginId(pluginId);
            extensionMapper.deleteByPluginId(pluginId);
            serviceMapper.deleteByPluginId(pluginId);
            
            // 删除插件主记录
            int deletedRows = pluginMapper.deletePlugin(pluginId);
            if (deletedRows > 0) {
                log.info("Deleted plugin from database: {}", pluginId);
            } else {
                log.warn("Plugin not found in database: {}", pluginId);
            }
            
            // 移除内存缓存
            pluginInstances.remove(pluginId);
            
        } catch (Exception e) {
            log.error("Failed to delete plugin: {}", pluginId, e);
            throw new RuntimeException("Failed to delete plugin: " + pluginId, e);
        }
    }
    
    @Override
    @Transactional
    public void delete(PluginInfo pluginInfo) {
        if (pluginInfo != null && pluginInfo.getPluginId() != null) {
            deleteByPluginId(pluginInfo.getPluginId());
        }
    }
    
    @Override
    @Transactional
    public void deleteAll() {
        log.info("Deleting all plugins");
        
        try {
            List<PluginEntity> allPlugins = pluginMapper.findAll();
            for (PluginEntity entity : allPlugins) {
                deleteByPluginId(entity.getPluginId());
            }
            
            pluginInstances.clear();
            log.info("Deleted all plugins");
            
        } catch (Exception e) {
            log.error("Failed to delete all plugins", e);
            throw new RuntimeException("Failed to delete all plugins", e);
        }
    }
    
    @Override
    public long count() {
        return pluginMapper.countAll();
    }
    
    @Override
    public long countByType(PluginType type) {
        if (type == null) {
            return 0;
        }
        return pluginMapper.findByType(type).size();
    }
    
    @Override
    public long countByStatus(PluginStatus status) {
        if (status == null) {
            return 0;
        }
        return pluginMapper.countByStatus(status);
    }
    
    @Override
    public long countByEnabled() {
        return countByStatus(PluginStatus.INSTALLED_ENABLED);
    }
    
    // ========== 扩展方法（非接口方法） ==========
    
    /**
     * 获取运行时插件实例
     * 
     * @param pluginId 插件ID
     * @return 插件实例
     */
    public Plugin getPluginInstance(String pluginId) {
        if (pluginId == null || pluginId.trim().isEmpty()) {
            return null;
        }
        
        // 从内存缓存获取运行时实例
        return pluginInstances.get(pluginId);
    }
    
    /**
     * 获取所有运行时插件实例
     * 
     * @return 插件实例集合
     */
    public Collection<Plugin> getAllPluginInstances() {
        return pluginInstances.values();
    }
    
    /**
     * 检查插件是否已注册（运行时）
     * 
     * @param pluginId 插件ID
     * @return 是否已注册
     */
    public boolean isPluginInstanceRegistered(String pluginId) {
        if (pluginId == null || pluginId.trim().isEmpty()) {
            return false;
        }
        
        return pluginInstances.containsKey(pluginId);
    }
    
    /**
     * 获取运行时插件实例数量
     * 
     * @return 插件实例数量
     */
    public int getPluginInstanceCount() {
        return pluginInstances.size();
    }
    
    /**
     * 获取数据库中的所有插件信息（包括未加载的）
     * 
     * @return 插件实体列表
     */
    public List<PluginEntity> getAllPluginEntities() {
        return pluginMapper.findAll();
    }
    
    /**
     * 根据状态获取插件实体
     * 
     * @param status 插件状态
     * @return 插件实体列表
     */
    public List<PluginEntity> getPluginEntitiesByStatus(PluginStatus status) {
        return pluginMapper.findByStatus(status);
    }
    
    /**
     * 根据类型获取插件实体
     * 
     * @param type 插件类型
     * @return 插件实体列表
     */
    public List<PluginEntity> getPluginEntitiesByType(PluginType type) {
        return pluginMapper.findByType(type);
    }
    
    /**
     * 更新插件状态
     * 
     * @param pluginId 插件ID
     * @param status 新状态
     */
    @Transactional
    public void updatePluginStatus(String pluginId, PluginStatus status) {
        pluginMapper.updateStatus(pluginId, status);
        log.info("Updated plugin {} status to {}", pluginId, status);
    }
    
    /**
     * 获取插件实体信息
     * 
     * @param pluginId 插件ID
     * @return 插件实体
     */
    public PluginEntity getPluginEntity(String pluginId) {
        return pluginMapper.findById(pluginId);
    }
    
    /**
     * 统计数据库中的插件数量
     * 
     * @return 插件总数
     */
    public long countAllPlugins() {
        return pluginMapper.countAll();
    }
    
    /**
     * 根据状态统计插件数量
     * 
     * @param status 插件状态
     * @return 插件数量
     */
    public long countPluginsByStatus(PluginStatus status) {
        return pluginMapper.countByStatus(status);
    }
    
    /**
     * 添加插件到内存缓存
     * 
     * @param plugin 插件实例
     */
    public void cachePluginInstance(Plugin plugin) {
        if (plugin != null) {
            pluginInstances.put(plugin.getId(), plugin);
            log.debug("Cached plugin instance: {}", plugin.getId());
        }
    }
    
    /**
     * 从内存缓存移除插件
     * 
     * @param pluginId 插件ID
     */
    public void removeCachedInstance(String pluginId) {
        pluginInstances.remove(pluginId);
        log.debug("Removed cached plugin instance: {}", pluginId);
    }
    
    /**
     * 将PluginInfo转换为数据库实体
     * 
     * @param pluginInfo 插件信息
     * @return 数据库实体
     */
    private PluginEntity convertToEntity(PluginInfo pluginInfo) {
        PluginEntity entity = new PluginEntity();
        entity.setPluginId(pluginInfo.getPluginId());
        entity.setName(pluginInfo.getName());
        entity.setVersion(pluginInfo.getVersion());
        entity.setDescription(pluginInfo.getDescription());
        entity.setAuthor(pluginInfo.getAuthor());
        entity.setType(pluginInfo.getType());
        entity.setStatus(pluginInfo.getStatus());
        entity.setPluginPath(pluginInfo.getPluginPath());
        entity.setMainClass(pluginInfo.getMainClass());
        entity.setIconPath(pluginInfo.getIconPath());
        // 时间字段处理
        if (pluginInfo.getInstallTime() != null) {
            entity.setInstalledAt(pluginInfo.getInstallTime());
        }
        if (pluginInfo.getUpdateTime() != null) {
            entity.setUpdatedAt(pluginInfo.getUpdateTime());
        }
        
        // 根据enabled状态设置对应时间
        if (Boolean.TRUE.equals(pluginInfo.getEnabled())) {
            entity.setEnabledAt(LocalDateTime.now());
        } else {
            entity.setDisabledAt(LocalDateTime.now());
        }
        
        return entity;
    }
    
    /**
     * 将数据库实体转换为PluginInfo
     * 
     * @param entity 数据库实体
     * @return 插件信息
     */
    private PluginInfo convertToPluginInfo(PluginEntity entity) {
        PluginInfo pluginInfo = new PluginInfo();
        pluginInfo.setPluginId(entity.getPluginId());
        pluginInfo.setName(entity.getName());
        pluginInfo.setVersion(entity.getVersion());
        pluginInfo.setDescription(entity.getDescription());
        pluginInfo.setAuthor(entity.getAuthor());
        pluginInfo.setType(entity.getType());
        pluginInfo.setStatus(entity.getStatus());
        pluginInfo.setPluginPath(entity.getPluginPath());
        pluginInfo.setMainClass(entity.getMainClass());
        pluginInfo.setIconPath(entity.getIconPath());
        // 时间字段处理
        if (entity.getInstalledAt() != null) {
            pluginInfo.setInstallTime(entity.getInstalledAt());
        }
        if (entity.getUpdatedAt() != null) {
            pluginInfo.setUpdateTime(entity.getUpdatedAt());
        }
        
        // 根据状态设置enabled字段
        pluginInfo.setEnabled(entity.getStatus() == PluginStatus.INSTALLED_ENABLED);
        
        return pluginInfo;
    }
    
    /**
     * 注册插件运行时实例
     * 
     * @param plugin 插件实例
     */
    public void registerPluginInstance(Plugin plugin) {
        if (plugin != null) {
            pluginInstances.put(plugin.getId(), plugin);
            log.debug("Registered plugin instance: {}", plugin.getId());
        }
    }
    
    /**
     * 注销插件运行时实例
     * 
     * @param pluginId 插件ID
     */
    public void unregisterPluginInstance(String pluginId) {
        if (pluginId != null) {
            pluginInstances.remove(pluginId);
            log.debug("Unregistered plugin instance: {}", pluginId);
        }
    }
}

