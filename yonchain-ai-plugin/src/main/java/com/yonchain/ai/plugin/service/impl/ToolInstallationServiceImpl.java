package com.yonchain.ai.plugin.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.yonchain.ai.plugin.entity.ToolInstallation;
import com.yonchain.ai.plugin.mapper.ToolInstallationMapper;
import com.yonchain.ai.plugin.service.ToolInstallationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 工具插件安装服务实现
 * 
 * @author yonchain
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ToolInstallationServiceImpl implements ToolInstallationService {

    private final ToolInstallationMapper toolInstallationMapper;
    private final ObjectMapper objectMapper;

    @Override
    @Transactional
    public ToolInstallation installToolPlugin(String tenantId, String toolName, String pluginId) {
        log.info("Installing tool plugin for tenant: {}, tool: {}", tenantId, toolName);
        
        // 检查是否已安装
        ToolInstallation existing = toolInstallationMapper.findByTenantIdAndToolName(tenantId, toolName);
        if (existing != null) {
            log.warn("Tool plugin already installed for tenant: {}, tool: {}", tenantId, toolName);
            return existing;
        }
        
        // 创建安装记录
        ToolInstallation installation = ToolInstallation.create(tenantId, toolName, pluginId);
        
        int result = toolInstallationMapper.insert(installation);
        if (result > 0) {
            log.info("Successfully installed tool plugin for tenant: {}, tool: {}", tenantId, toolName);
            return installation;
        } else {
            throw new RuntimeException("Failed to install tool plugin");
        }
    }

    @Override
    @Transactional
    public ToolInstallation installToolPlugin(String tenantId, String toolName, String pluginId, String pluginUniqueIdentifier) {
        log.info("Installing tool plugin for tenant: {}, tool: {}, uniqueId: {}", tenantId, toolName, pluginUniqueIdentifier);
        
        // 检查是否已安装
        ToolInstallation existing = toolInstallationMapper.findByTenantIdAndToolName(tenantId, toolName);
        if (existing != null) {
            log.warn("Tool plugin already installed for tenant: {}, tool: {}", tenantId, toolName);
            return existing;
        }
        
        // 创建安装记录
        ToolInstallation installation = ToolInstallation.create(tenantId, toolName, pluginId, pluginUniqueIdentifier);
        
        int result = toolInstallationMapper.insert(installation);
        if (result > 0) {
            log.info("Successfully installed tool plugin for tenant: {}, tool: {}", tenantId, toolName);
            return installation;
        } else {
            throw new RuntimeException("Failed to install tool plugin");
        }
    }

    @Override
    @Transactional
    public ToolInstallation installToolPlugin(String tenantId, String toolName, String pluginId, String config, Boolean enabled) {
        log.info("Installing tool plugin for tenant: {}, tool: {}, config provided: {}", tenantId, toolName, config != null);
        
        // 检查是否已安装
        ToolInstallation existing = toolInstallationMapper.findByTenantIdAndToolName(tenantId, toolName);
        if (existing != null) {
            log.warn("Tool plugin already installed for tenant: {}, tool: {}", tenantId, toolName);
            return existing;
        }
        
        // 创建安装记录
        ToolInstallation installation = ToolInstallation.create(tenantId, toolName, pluginId, config, enabled);
        
        int result = toolInstallationMapper.insert(installation);
        if (result > 0) {
            log.info("Successfully installed tool plugin for tenant: {}, tool: {}", tenantId, toolName);
            return installation;
        } else {
            throw new RuntimeException("Failed to install tool plugin");
        }
    }

    @Override
    @Transactional
    public boolean uninstallToolPlugin(String tenantId, String toolName) {
        log.info("Uninstalling tool plugin for tenant: {}, tool: {}", tenantId, toolName);
        
        int result = toolInstallationMapper.deleteByTenantIdAndToolName(tenantId, toolName);
        if (result > 0) {
            log.info("Successfully uninstalled tool plugin for tenant: {}, tool: {}", tenantId, toolName);
            return true;
        } else {
            log.warn("Tool plugin not found for tenant: {}, tool: {}", tenantId, toolName);
            return false;
        }
    }

    @Override
    @Transactional
    public boolean enableToolPlugin(String tenantId, String toolName) {
        log.info("Enabling tool plugin for tenant: {}, tool: {}", tenantId, toolName);
        
        ToolInstallation installation = toolInstallationMapper.findByTenantIdAndToolName(tenantId, toolName);
        if (installation == null) {
            log.warn("Tool plugin not found for tenant: {}, tool: {}", tenantId, toolName);
            return false;
        }
        
        installation.enable();
        int result = toolInstallationMapper.updateEnabled(installation.getId(), installation.getEnabled(), installation.getUpdatedAt());
        
        if (result > 0) {
            log.info("Successfully enabled tool plugin for tenant: {}, tool: {}", tenantId, toolName);
            return true;
        } else {
            return false;
        }
    }

    @Override
    @Transactional
    public boolean disableToolPlugin(String tenantId, String toolName) {
        log.info("Disabling tool plugin for tenant: {}, tool: {}", tenantId, toolName);
        
        ToolInstallation installation = toolInstallationMapper.findByTenantIdAndToolName(tenantId, toolName);
        if (installation == null) {
            log.warn("Tool plugin not found for tenant: {}, tool: {}", tenantId, toolName);
            return false;
        }
        
        installation.disable();
        int result = toolInstallationMapper.updateEnabled(installation.getId(), installation.getEnabled(), installation.getUpdatedAt());
        
        if (result > 0) {
            log.info("Successfully disabled tool plugin for tenant: {}, tool: {}", tenantId, toolName);
            return true;
        } else {
            return false;
        }
    }

    @Override
    @Transactional
    public boolean toggleToolPlugin(String tenantId, String toolName, boolean enabled) {
        log.info("Toggling tool plugin for tenant: {}, tool: {}, enabled: {}", tenantId, toolName, enabled);
        
        ToolInstallation installation = toolInstallationMapper.findByTenantIdAndToolName(tenantId, toolName);
        if (installation == null) {
            log.warn("Tool plugin not found for tenant: {}, tool: {}", tenantId, toolName);
            return false;
        }
        
        installation.setEnabledStatus(enabled);
        int result = toolInstallationMapper.updateEnabled(installation.getId(), installation.getEnabled(), installation.getUpdatedAt());
        
        if (result > 0) {
            log.info("Successfully toggled tool plugin for tenant: {}, tool: {}, enabled: {}", tenantId, toolName, enabled);
            return true;
        } else {
            return false;
        }
    }

    @Override
    @Transactional
    public ToolInstallation updateToolConfig(String tenantId, String toolName, String config) {
        log.info("Updating tool config for tenant: {}, tool: {}", tenantId, toolName);
        
        ToolInstallation installation = toolInstallationMapper.findByTenantIdAndToolName(tenantId, toolName);
        if (installation == null) {
            throw new RuntimeException("Tool plugin not found for tenant: " + tenantId + ", tool: " + toolName);
        }
        
        installation.updateConfig(config);
        int result = toolInstallationMapper.updateConfig(installation.getId(), installation.getConfig(), installation.getUpdatedAt());
        
        if (result > 0) {
            log.info("Successfully updated tool config for tenant: {}, tool: {}", tenantId, toolName);
            return installation;
        } else {
            throw new RuntimeException("Failed to update tool config");
        }
    }

    @Override
    public List<ToolInstallation> getTenantToolInstallations(String tenantId) {
        log.debug("Getting tool installations for tenant: {}", tenantId);
        return toolInstallationMapper.findByTenantId(tenantId);
    }

    @Override
    public List<ToolInstallation> getTenantEnabledToolInstallations(String tenantId) {
        log.debug("Getting enabled tool installations for tenant: {}", tenantId);
        return toolInstallationMapper.findByTenantIdAndEnabled(tenantId, true);
    }

    @Override
    public List<String> getTenantInstalledToolNames(String tenantId) {
        log.debug("Getting installed tool names for tenant: {}", tenantId);
        List<ToolInstallation> installations = toolInstallationMapper.findByTenantId(tenantId);
        return installations.stream()
                .map(ToolInstallation::getToolName)
                .distinct()
                .collect(Collectors.toList());
    }

    @Override
    public List<String> getTenantEnabledToolNames(String tenantId) {
        log.debug("Getting enabled tool names for tenant: {}", tenantId);
        List<ToolInstallation> installations = toolInstallationMapper.findByTenantIdAndEnabled(tenantId, true);
        return installations.stream()
                .map(ToolInstallation::getToolName)
                .distinct()
                .collect(Collectors.toList());
    }

    @Override
    public boolean isToolInstalled(String tenantId, String toolName) {
        log.debug("Checking if tool is installed for tenant: {}, tool: {}", tenantId, toolName);
        return toolInstallationMapper.existsByTenantIdAndToolName(tenantId, toolName);
    }

    @Override
    public ToolInstallation getById(String id) {
        log.debug("Getting tool installation by id: {}", id);
        return toolInstallationMapper.findById(id);
    }

    @Override
    public ToolInstallation getByTenantAndTool(String tenantId, String toolName) {
        log.debug("Getting tool installation for tenant: {}, tool: {}", tenantId, toolName);
        return toolInstallationMapper.findByTenantIdAndToolName(tenantId, toolName);
    }

    @Override
    public List<ToolInstallation> getByPluginId(String pluginId) {
        log.debug("Getting tool installations by plugin id: {}", pluginId);
        return toolInstallationMapper.findByPluginId(pluginId);
    }

    @Override
    public List<ToolInstallation> getByToolName(String toolName) {
        log.debug("Getting tool installations by tool name: {}", toolName);
        return toolInstallationMapper.findByToolName(toolName);
    }

    @Override
    @Transactional
    public boolean deleteById(String id) {
        log.info("Deleting tool installation: {}", id);
        
        int result = toolInstallationMapper.deleteById(id);
        if (result > 0) {
            log.info("Successfully deleted tool installation: {}", id);
            return true;
        } else {
            log.warn("Tool installation not found: {}", id);
            return false;
        }
    }

    @Override
    public long countByTenantId(String tenantId) {
        log.debug("Counting tool installations for tenant: {}", tenantId);
        return toolInstallationMapper.countByTenantId(tenantId);
    }

    @Override
    public long countByToolName(String toolName) {
        log.debug("Counting tool installations for tool: {}", toolName);
        return toolInstallationMapper.countByToolName(toolName);
    }

    @Override
    public long countEnabled() {
        log.debug("Counting enabled tool installations");
        return toolInstallationMapper.countEnabled();
    }

    @Override
    public boolean existsByTenantIdAndToolName(String tenantId, String toolName) {
        log.debug("Checking existence for tenant: {}, tool: {}", tenantId, toolName);
        return toolInstallationMapper.existsByTenantIdAndToolName(tenantId, toolName);
    }
}


