package com.yonchain.ai.plugin.service.impl;

import com.yonchain.ai.plugin.entity.AiModelInstallation;
import com.yonchain.ai.plugin.mapper.AiModelInstallationMapper;
import com.yonchain.ai.plugin.service.AiModelInstallationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * AI模型插件安装服务实现
 * 
 * @author yonchain
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AiModelInstallationServiceImpl implements AiModelInstallationService {

    private final AiModelInstallationMapper aiModelInstallationMapper;

    @Override
    @Transactional
    public AiModelInstallation installAiModelPlugin(String tenantId, String provider, String pluginId) {
        log.info("Installing AI model plugin for tenant: {}, provider: {}", tenantId, provider);
        
        // 检查是否已安装
        AiModelInstallation existing = aiModelInstallationMapper.findByTenantIdAndProvider(tenantId, provider);
        if (existing != null) {
            log.warn("AI model plugin already installed for tenant: {}, provider: {}", tenantId, provider);
            return existing;
        }
        
        // 创建安装记录
        AiModelInstallation installation = AiModelInstallation.create(tenantId, provider, pluginId);
        
        int result = aiModelInstallationMapper.insert(installation);
        if (result > 0) {
            log.info("Successfully installed AI model plugin for tenant: {}, provider: {}", tenantId, provider);
            return installation;
        } else {
            throw new RuntimeException("Failed to install AI model plugin");
        }
    }

    @Override
    @Transactional
    public AiModelInstallation installAiModelPlugin(String tenantId, String provider, String pluginId, String pluginUniqueIdentifier) {
        log.info("Installing AI model plugin for tenant: {}, provider: {}, uniqueId: {}", tenantId, provider, pluginUniqueIdentifier);
        
        // 检查是否已安装
        AiModelInstallation existing = aiModelInstallationMapper.findByTenantIdAndProvider(tenantId, provider);
        if (existing != null) {
            log.warn("AI model plugin already installed for tenant: {}, provider: {}", tenantId, provider);
            return existing;
        }
        
        // 创建安装记录
        AiModelInstallation installation = AiModelInstallation.create(tenantId, provider, pluginId, pluginUniqueIdentifier);
        
        int result = aiModelInstallationMapper.insert(installation);
        if (result > 0) {
            log.info("Successfully installed AI model plugin for tenant: {}, provider: {}", tenantId, provider);
            return installation;
        } else {
            throw new RuntimeException("Failed to install AI model plugin");
        }
    }

    @Override
    @Transactional
    public boolean uninstallAiModelPlugin(String tenantId, String provider) {
        log.info("Uninstalling AI model plugin for tenant: {}, provider: {}", tenantId, provider);
        
        int result = aiModelInstallationMapper.deleteByTenantIdAndProvider(tenantId, provider);
        if (result > 0) {
            log.info("Successfully uninstalled AI model plugin for tenant: {}, provider: {}", tenantId, provider);
            return true;
        } else {
            log.warn("AI model plugin not found for tenant: {}, provider: {}", tenantId, provider);
            return false;
        }
    }

    @Override
    public List<AiModelInstallation> getTenantAiModelInstallations(String tenantId) {
        log.debug("Getting AI model installations for tenant: {}", tenantId);
        return aiModelInstallationMapper.findByTenantId(tenantId);
    }

    @Override
    public List<String> getTenantInstalledProviders(String tenantId) {
        log.debug("Getting installed providers for tenant: {}", tenantId);
        List<AiModelInstallation> installations = aiModelInstallationMapper.findByTenantId(tenantId);
        return installations.stream()
                .map(AiModelInstallation::getProvider)
                .distinct()
                .collect(Collectors.toList());
    }

    @Override
    public boolean isProviderInstalled(String tenantId, String provider) {
        log.debug("Checking if provider is installed for tenant: {}, provider: {}", tenantId, provider);
        long count = aiModelInstallationMapper.countByTenantId(tenantId);
        return count > 0 && aiModelInstallationMapper.findByTenantIdAndProvider(tenantId, provider) != null;
    }

    @Override
    public AiModelInstallation getById(String id) {
        log.debug("Getting AI model installation by id: {}", id);
        return aiModelInstallationMapper.findById(id);
    }

    @Override
    public AiModelInstallation getByTenantAndProvider(String tenantId, String provider) {
        log.debug("Getting AI model installation for tenant: {}, provider: {}", tenantId, provider);
        return aiModelInstallationMapper.findByTenantIdAndProvider(tenantId, provider);
    }

    @Override
    public List<AiModelInstallation> getByPluginId(String pluginId) {
        log.debug("Getting AI model installations by plugin id: {}", pluginId);
        return aiModelInstallationMapper.findByPluginId(pluginId);
    }

    @Override
    public List<AiModelInstallation> getByProvider(String provider) {
        log.debug("Getting AI model installations by provider: {}", provider);
        return aiModelInstallationMapper.findByProvider(provider);
    }

    @Override
    @Transactional
    public AiModelInstallation updatePluginUniqueIdentifier(String id, String pluginUniqueIdentifier) {
        log.info("Updating plugin unique identifier for installation: {}", id);
        
        AiModelInstallation installation = aiModelInstallationMapper.findById(id);
        if (installation == null) {
            throw new RuntimeException("AI model installation not found: " + id);
        }
        
        installation.updatePluginUniqueIdentifier(pluginUniqueIdentifier);
        int result = aiModelInstallationMapper.updatePluginUniqueIdentifier(id, pluginUniqueIdentifier, installation.getUpdatedAt());
        
        if (result > 0) {
            log.info("Successfully updated plugin unique identifier for installation: {}", id);
            return installation;
        } else {
            throw new RuntimeException("Failed to update plugin unique identifier");
        }
    }

    @Override
    @Transactional
    public boolean deleteById(String id) {
        log.info("Deleting AI model installation: {}", id);
        
        int result = aiModelInstallationMapper.deleteById(id);
        if (result > 0) {
            log.info("Successfully deleted AI model installation: {}", id);
            return true;
        } else {
            log.warn("AI model installation not found: {}", id);
            return false;
        }
    }

    @Override
    public long countByTenantId(String tenantId) {
        log.debug("Counting AI model installations for tenant: {}", tenantId);
        return aiModelInstallationMapper.countByTenantId(tenantId);
    }

    @Override
    public long countByProvider(String provider) {
        log.debug("Counting AI model installations for provider: {}", provider);
        return aiModelInstallationMapper.countByProvider(provider);
    }

    @Override
    public boolean existsByTenantIdAndProvider(String tenantId, String provider) {
        log.debug("Checking existence for tenant: {}, provider: {}", tenantId, provider);
        return aiModelInstallationMapper.existsByTenantIdAndProvider(tenantId, provider);
    }
}


