package com.yonchain.ai.model.registry;

import com.yonchain.ai.api.exception.YonchainException;
import com.yonchain.ai.api.model.ModelInfo;
import com.yonchain.ai.api.model.ModelProvider;
import com.yonchain.ai.model.entity.ModelEntity;
import com.yonchain.ai.model.entity.ModelProviderEntity;
import com.yonchain.ai.model.loader.ModelLoader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * 模型注册表初始化器
 * <p>
 * 在应用启动时加载模型配置信息，但不立即创建模型实例
 * 遵循单一职责原则，只负责初始化配置
 * 
 * @author Cgy
 */
@Component
public class ModelRegistryInitializer implements InitializingBean {

    private static final Logger logger = LoggerFactory.getLogger(ModelRegistryInitializer.class);

    @Autowired
    private ModelLoader modelLoader;

    @Autowired
    private ModelRegistry modelRegistry;
    
    /**
     * 在应用启动时加载模型配置信息
     */
    @Override
    public void afterPropertiesSet() throws Exception {
        initializeModelRegistry();
    }

    /**
     * 初始化模型注册表
     * 只加载配置信息，不创建实际的模型实例
     */
    public void initializeModelRegistry() {
        logger.info("开始初始化模型注册表配置...");
        
        try {
            // 加载所有模型和提供商配置
            Collection<? extends ModelInfo> models = modelLoader.loadModels();
            Collection<? extends ModelProvider> providers = modelLoader.loadProviders();
            
            // 将模型信息转换为ModelEntity
            Map<String, ModelEntity> modelMap = convertToModelEntities(models);
            
            // 将提供商信息转换为ModelProviderEntity
            Map<String, ModelProviderEntity> providerMap = convertToProviderEntities(providers);
            
            // 初始化模型配置
            int configuredCount = initializeModelConfigurations(modelMap, providerMap);
            
            logger.info("模型注册表配置初始化完成，共配置 {} 个模型", configuredCount);
        } catch (Exception e) {
            logger.error("初始化模型注册表配置失败", e);
            throw new YonchainException("模型注册表配置初始化失败: " + e.getMessage(), e);
        }
    }
    
    /**
     * 将ModelInfo集合转换为ModelEntity映射
     */
    private Map<String, ModelEntity> convertToModelEntities(Collection<? extends ModelInfo> models) {
        Map<String, ModelEntity> modelMap = new HashMap<>(models.size());
        for (ModelInfo model : models) {
            ModelEntity modelEntity = new ModelEntity();
            modelEntity.setModelCode(model.getCode());
            modelEntity.setProviderCode(model.getProvider());
            modelEntity.setEnabled(model.getEnabled());
            
            modelMap.put(model.getCode(), modelEntity);
        }
        return modelMap;
    }
    
    /**
     * 将ModelProvider集合转换为ModelProviderEntity映射
     */
    private Map<String, ModelProviderEntity> convertToProviderEntities(Collection<? extends ModelProvider> providers) {
        Map<String, ModelProviderEntity> providerMap = new HashMap<>(providers.size());
        for (ModelProvider provider : providers) {
            ModelProviderEntity providerEntity = new ModelProviderEntity();
            providerEntity.setProviderCode(provider.getCode());
            providerEntity.setEnabled(provider.getEnabled());
            
            // 提取配置信息，但不设置API Key（将在实际使用时从配置或数据库获取）
            provider.getConfigSchemas().forEach(configItem -> {
                if ("baseUrl".equals(configItem.getName())) {
                    providerEntity.setBaseUrl((String) configItem.getValue());
                }
                // 不在启动时加载API Key
            });
            
            providerMap.put(provider.getCode(), providerEntity);
        }
        return providerMap;
    }
    
    /**
     * 初始化模型配置信息，但不立即创建模型实例
     * @return 成功配置的模型数量
     */
    private int initializeModelConfigurations(Map<String, ModelEntity> modelMap, Map<String, ModelProviderEntity> providerMap) {
        int configuredCount = 0;
        
        for (ModelEntity model : modelMap.values()) {
            // 只处理启用的模型
            if (model.getEnabled() == null || !model.getEnabled()) {
                logger.debug("跳过未启用的模型: {}", model.getModelCode());
                continue;
            }
            
            String providerCode = model.getProviderCode();
            ModelProviderEntity provider = providerMap.get(providerCode);
            
            // 确保提供商存在且已启用
            if (provider == null) {
                logger.warn("模型 {} 的提供商 {} 不存在", model.getModelCode(), providerCode);
                continue;
            }
            
            if (provider.getEnabled() == null || !provider.getEnabled()) {
                logger.debug("跳过未启用的提供商: {}", provider.getProviderCode());
                continue;
            }
            
            // 生成模型ID
            String modelId = model.getModelCode() + "-" + provider.getProviderCode();
            
            // 将模型静态信息注册到注册表
            modelRegistry.registerModelInfo(modelId, model, provider);
            logger.info("已注册模型静态信息到注册表: {}", modelId);
            
            configuredCount++;
        }
        
        return configuredCount;
    }
}