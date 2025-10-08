package com.yonchain.ai.plugin.service.impl;

import com.yonchain.ai.plugin.entity.PluginInstallation;
import com.yonchain.ai.plugin.mapper.PluginInstallationMapper;
import com.yonchain.ai.plugin.service.PluginInstallationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 插件安装服务实现
 * 
 * @author yonchain
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class PluginInstallationServiceImpl implements PluginInstallationService {

    private final PluginInstallationMapper pluginInstallationMapper;

    @Override
    @Transactional
    public PluginInstallation installPlugin(String tenantId, String pluginId, String runtimeType) {
        log.info("Installing plugin for tenant: {}, plugin: {}, runtimeType: {}", tenantId, pluginId, runtimeType);
        
        // 检查是否已安装
        PluginInstallation existing = pluginInstallationMapper.findByTenantIdAndPluginId(tenantId, pluginId);
        if (existing != null) {
            log.warn("Plugin already installed for tenant: {}, plugin: {}", tenantId, pluginId);
            return existing;
        }
        
        // 创建安装记录
        PluginInstallation installation = PluginInstallation.create(tenantId, pluginId, runtimeType);
        
        int result = pluginInstallationMapper.insert(installation);
        if (result > 0) {
            log.info("Successfully installed plugin for tenant: {}, plugin: {}", tenantId, pluginId);
            return installation;
        } else {
            throw new RuntimeException("Failed to install plugin");
        }
    }

    @Override
    @Transactional
    public PluginInstallation installPlugin(String tenantId, String pluginId, String runtimeType, String pluginUniqueIdentifier) {
        log.info("Installing plugin for tenant: {}, plugin: {}, runtimeType: {}, uniqueId: {}", tenantId, pluginId, runtimeType, pluginUniqueIdentifier);
        
        // 检查是否已安装
        PluginInstallation existing = pluginInstallationMapper.findByTenantIdAndPluginId(tenantId, pluginId);
        if (existing != null) {
            log.warn("Plugin already installed for tenant: {}, plugin: {}", tenantId, pluginId);
            return existing;
        }
        
        // 创建安装记录
        PluginInstallation installation = PluginInstallation.create(tenantId, pluginId, runtimeType, pluginUniqueIdentifier);
        
        int result = pluginInstallationMapper.insert(installation);
        if (result > 0) {
            log.info("Successfully installed plugin for tenant: {}, plugin: {}", tenantId, pluginId);
            return installation;
        } else {
            throw new RuntimeException("Failed to install plugin");
        }
    }

    @Override
    @Transactional
    public PluginInstallation installPlugin(String tenantId, String pluginId, String runtimeType, String pluginUniqueIdentifier, String meta) {
        log.info("Installing plugin for tenant: {}, plugin: {}, runtimeType: {}, meta provided: {}", tenantId, pluginId, runtimeType, meta != null);
        
        // 检查是否已安装
        PluginInstallation existing = pluginInstallationMapper.findByTenantIdAndPluginId(tenantId, pluginId);
        if (existing != null) {
            log.warn("Plugin already installed for tenant: {}, plugin: {}", tenantId, pluginId);
            return existing;
        }
        
        // 创建安装记录
        PluginInstallation installation = PluginInstallation.create(tenantId, pluginId, runtimeType, pluginUniqueIdentifier, meta);
        
        int result = pluginInstallationMapper.insert(installation);
        if (result > 0) {
            log.info("Successfully installed plugin for tenant: {}, plugin: {}", tenantId, pluginId);
            return installation;
        } else {
            throw new RuntimeException("Failed to install plugin");
        }
    }

    @Override
    @Transactional
    public boolean uninstallPlugin(String tenantId, String pluginId) {
        log.info("Uninstalling plugin for tenant: {}, plugin: {}", tenantId, pluginId);
        
        int result = pluginInstallationMapper.deleteByTenantIdAndPluginId(tenantId, pluginId);
        if (result > 0) {
            log.info("Successfully uninstalled plugin for tenant: {}, plugin: {}", tenantId, pluginId);
            return true;
        } else {
            log.warn("Plugin not found for tenant: {}, plugin: {}", tenantId, pluginId);
            return false;
        }
    }

    @Override
    public List<PluginInstallation> getInstalledPlugins(String tenantId) {
        if (tenantId == null) {
            return Collections.emptyList();
        }

        try {
            return pluginInstallationMapper.findByTenantId(tenantId);
        } catch (Exception e) {
            log.error("Failed to get installed plugins for tenant: {}", tenantId, e);
            return Collections.emptyList();
        }
    }

    @Override
    public List<String> getInstalledPluginIds(String tenantId) {
        if (tenantId == null) {
            return Collections.emptyList();
        }

        try {
            List<PluginInstallation> installations = pluginInstallationMapper.findByTenantId(tenantId);
            return installations.stream()
                    .map(PluginInstallation::getPluginId)
                    .collect(Collectors.toList());
        } catch (Exception e) {
            log.error("Failed to get installed plugin IDs for tenant: {}", tenantId, e);
            return Collections.emptyList();
        }
    }

    @Override
    public boolean isPluginInstalled(String tenantId, String pluginId) {
        if (tenantId == null || pluginId == null) {
            return false;
        }

        try {
            return pluginInstallationMapper.existsByTenantIdAndPluginId(tenantId, pluginId);
        } catch (Exception e) {
            log.error("Failed to check plugin installation for tenant: {}, plugin: {}", tenantId, pluginId, e);
            return false;
        }
    }

    @Override
    public PluginInstallation getPluginInstallation(String tenantId, String pluginId) {
        if (tenantId == null || pluginId == null) {
            return null;
        }

        try {
            return pluginInstallationMapper.findByTenantIdAndPluginId(tenantId, pluginId);
        } catch (Exception e) {
            log.error("Failed to get plugin installation for tenant: {}, plugin: {}", tenantId, pluginId, e);
            return null;
        }
    }

    @Override
    public List<PluginInstallation> getInstalledPluginsByRuntimeType(String tenantId, String runtimeType) {
        if (tenantId == null || runtimeType == null) {
            return Collections.emptyList();
        }

        try {
            return pluginInstallationMapper.findByTenantIdAndRuntimeType(tenantId, runtimeType);
        } catch (Exception e) {
            log.error("Failed to get installed plugins by runtime type for tenant: {}, runtimeType: {}", tenantId, runtimeType, e);
            return Collections.emptyList();
        }
    }

    @Override
    @Transactional
    public boolean updatePluginEndpoints(String tenantId, String pluginId, Long endpointsSetups, Long endpointsActive) {
        log.info("Updating plugin endpoints for tenant: {}, plugin: {}, setups: {}, active: {}", tenantId, pluginId, endpointsSetups, endpointsActive);
        
        PluginInstallation installation = pluginInstallationMapper.findByTenantIdAndPluginId(tenantId, pluginId);
        if (installation == null) {
            log.warn("Plugin installation not found for tenant: {}, plugin: {}", tenantId, pluginId);
            return false;
        }
        
        int result = pluginInstallationMapper.updateEndpoints(installation.getId(), endpointsSetups, endpointsActive, LocalDateTime.now());
        if (result > 0) {
            log.info("Successfully updated plugin endpoints for tenant: {}, plugin: {}", tenantId, pluginId);
            return true;
        } else {
            log.warn("Failed to update plugin endpoints for tenant: {}, plugin: {}", tenantId, pluginId);
            return false;
        }
    }

    @Override
    @Transactional
    public boolean updatePluginMeta(String tenantId, String pluginId, String meta) {
        log.info("Updating plugin meta for tenant: {}, plugin: {}, meta provided: {}", tenantId, pluginId, meta != null);
        
        PluginInstallation installation = pluginInstallationMapper.findByTenantIdAndPluginId(tenantId, pluginId);
        if (installation == null) {
            log.warn("Plugin installation not found for tenant: {}, plugin: {}", tenantId, pluginId);
            return false;
        }
        
        int result = pluginInstallationMapper.updateMeta(installation.getId(), meta, LocalDateTime.now());
        if (result > 0) {
            log.info("Successfully updated plugin meta for tenant: {}, plugin: {}", tenantId, pluginId);
            return true;
        } else {
            log.warn("Failed to update plugin meta for tenant: {}, plugin: {}", tenantId, pluginId);
            return false;
        }
    }

    @Override
    public List<com.yonchain.ai.plugin.entity.PluginEntity> getTenantPluginsByTenantId(String tenantId) {
        if (tenantId == null) {
            return Collections.emptyList();
        }

        try {
            return pluginInstallationMapper.findTenantPluginsByTenantId(tenantId);
        } catch (Exception e) {
            log.error("Failed to get tenant plugins by tenant ID: {}", tenantId, e);
            return Collections.emptyList();
        }
    }

    @Override
    public List<com.yonchain.ai.plugin.entity.PluginEntity> getTenantPluginsWithConditions(String tenantId, String status, String type, String name, int pageNum, int pageSize) {
        if (tenantId == null) {
            return Collections.emptyList();
        }

        try {
            int offset = (pageNum - 1) * pageSize;
            return pluginInstallationMapper.findTenantPluginsByTenantIdWithConditions(
                    tenantId, status, type, name, pageNum, pageSize, offset);
        } catch (Exception e) {
            log.error("Failed to get tenant plugins with conditions for tenant: {}", tenantId, e);
            return Collections.emptyList();
        }
    }

    @Override
    public long countTenantPluginsWithConditions(String tenantId, String status, String type, String name) {
        if (tenantId == null) {
            return 0L;
        }

        try {
            return pluginInstallationMapper.countTenantPluginsByTenantIdWithConditions(tenantId, status, type, name);
        } catch (Exception e) {
            log.error("Failed to count tenant plugins with conditions for tenant: {}", tenantId, e);
            return 0L;
        }
    }
}
