package com.yonchain.ai.plugin.registry.impl;

import com.yonchain.ai.plugin.entity.PluginInfo;
import com.yonchain.ai.plugin.enums.PluginStatus;
import com.yonchain.ai.plugin.enums.PluginType;
import com.yonchain.ai.plugin.registry.PluginRegistry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

/**
 * 内存插件注册中心实现
 * 
 * @author yonchain
 */
@Component
public class InMemoryPluginRegistry implements PluginRegistry {
    
    private static final Logger log = LoggerFactory.getLogger(InMemoryPluginRegistry.class);
    
    private final Map<String, PluginInfo> plugins = new ConcurrentHashMap<>();
    private final AtomicLong idGenerator = new AtomicLong(1);
    
    @Override
    public PluginInfo save(PluginInfo pluginInfo) {
        if (pluginInfo == null) {
            throw new IllegalArgumentException("Plugin info cannot be null");
        }
        
        if (pluginInfo.getPluginId() == null || pluginInfo.getPluginId().trim().isEmpty()) {
            throw new IllegalArgumentException("Plugin ID cannot be null or empty");
        }
        
        // 设置自增ID（如果没有的话）
        if (pluginInfo.getId() == null) {
            pluginInfo.setId(idGenerator.getAndIncrement());
        }
        
        String pluginId = pluginInfo.getPluginId();
        PluginInfo saved = plugins.put(pluginId, pluginInfo);
        
        if (saved == null) {
            log.debug("Saved new plugin: {}", pluginId);
        } else {
            log.debug("Updated existing plugin: {}", pluginId);
        }
        
        return pluginInfo;
    }
    
    @Override
    public Optional<PluginInfo> findByPluginId(String pluginId) {
        if (pluginId == null || pluginId.trim().isEmpty()) {
            return Optional.empty();
        }
        
        return Optional.ofNullable(plugins.get(pluginId));
    }
    
    @Override
    public List<PluginInfo> findByName(String name) {
        if (name == null || name.trim().isEmpty()) {
            return Collections.emptyList();
        }
        
        return plugins.values().stream()
                .filter(plugin -> name.equals(plugin.getName()))
                .collect(Collectors.toList());
    }
    
    @Override
    public List<PluginInfo> findByType(PluginType type) {
        if (type == null) {
            return Collections.emptyList();
        }
        
        return plugins.values().stream()
                .filter(plugin -> type.equals(plugin.getType()))
                .collect(Collectors.toList());
    }
    
    @Override
    public List<PluginInfo> findByStatus(PluginStatus status) {
        if (status == null) {
            return Collections.emptyList();
        }
        
        return plugins.values().stream()
                .filter(plugin -> status.equals(plugin.getStatus()))
                .collect(Collectors.toList());
    }
    
    @Override
    public List<PluginInfo> findByEnabled(boolean enabled) {
        return plugins.values().stream()
                .filter(plugin -> enabled == Boolean.TRUE.equals(plugin.getEnabled()))
                .collect(Collectors.toList());
    }
    
    @Override
    public List<PluginInfo> findByTypeAndStatus(PluginType type, PluginStatus status) {
        if (type == null || status == null) {
            return Collections.emptyList();
        }
        
        return plugins.values().stream()
                .filter(plugin -> type.equals(plugin.getType()) && status.equals(plugin.getStatus()))
                .collect(Collectors.toList());
    }
    
    @Override
    public List<PluginInfo> findAll() {
        return new ArrayList<>(plugins.values());
    }
    
    @Override
    public boolean existsByPluginId(String pluginId) {
        if (pluginId == null || pluginId.trim().isEmpty()) {
            return false;
        }
        
        return plugins.containsKey(pluginId);
    }
    
    @Override
    public void deleteByPluginId(String pluginId) {
        if (pluginId == null || pluginId.trim().isEmpty()) {
            return;
        }
        
        PluginInfo removed = plugins.remove(pluginId);
        if (removed != null) {
            log.debug("Deleted plugin: {}", pluginId);
        } else {
            log.debug("Plugin not found for deletion: {}", pluginId);
        }
    }
    
    @Override
    public void delete(PluginInfo pluginInfo) {
        if (pluginInfo == null || pluginInfo.getPluginId() == null) {
            return;
        }
        
        deleteByPluginId(pluginInfo.getPluginId());
    }
    
    @Override
    public void deleteAll() {
        int count = plugins.size();
        plugins.clear();
        log.debug("Deleted all {} plugins", count);
    }
    
    @Override
    public long count() {
        return plugins.size();
    }
    
    @Override
    public long countByType(PluginType type) {
        if (type == null) {
            return 0;
        }
        
        return plugins.values().stream()
                .filter(plugin -> type.equals(plugin.getType()))
                .count();
    }
    
    @Override
    public long countByStatus(PluginStatus status) {
        if (status == null) {
            return 0;
        }
        
        return plugins.values().stream()
                .filter(plugin -> status.equals(plugin.getStatus()))
                .count();
    }
    
    @Override
    public long countByEnabled() {
        return plugins.values().stream()
                .filter(plugin -> Boolean.TRUE.equals(plugin.getEnabled()))
                .count();
    }
    
    /**
     * 获取所有插件ID
     * 
     * @return 插件ID集合
     */
    public Set<String> getAllPluginIds() {
        return new HashSet<>(plugins.keySet());
    }
    
    /**
     * 清空缓存并重置ID生成器
     * 主要用于测试
     */
    public void reset() {
        plugins.clear();
        idGenerator.set(1);
        log.debug("Registry reset completed");
    }
    
    /**
     * 获取注册中心状态信息
     * 
     * @return 状态信息
     */
    public String getStatus() {
        return String.format("InMemoryPluginRegistry: %d plugins registered", plugins.size());
    }
}

