package com.yonchain.ai.model.registry;

import com.yonchain.ai.api.exception.YonchainException;
import com.yonchain.ai.api.model.ModelInfo;
import com.yonchain.ai.api.model.ModelProvider;
import com.yonchain.ai.model.entity.ModelEntity;
import com.yonchain.ai.model.entity.ModelProviderEntity;
import com.yonchain.ai.model.factory.ModelFactory;
import com.yonchain.ai.model.loader.ModelLoader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * 模型注册表初始化器
 * <p>
 * 在应用启动时加载模型并注册到注册表
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

    @Autowired
    private ModelFactory modelFactory;

    /**
     * 在应用启动时加载模型并注册到注册表
     */
    @Override
    public void afterPropertiesSet() throws Exception {
        initializeModelRegistry();
    }

    /**
     * 初始化模型注册表
     */
    public void initializeModelRegistry() {
        logger.info("开始初始化模型注册表...");
        
        try {
            // 加载所有模型和提供商
            Collection<? extends ModelInfo> models = modelLoader.loadModels();
            Collection<? extends ModelProvider> providers = modelLoader.loadProviders();
            
            // 将模型信息转换为ModelEntity
            Map<String, ModelEntity> modelMap = convertToModelEntities(models);
            
            // 将提供商信息转换为ModelProviderEntity
            Map<String, ModelProviderEntity> providerMap = convertToProviderEntities(providers);
            
            // 注册模型
            int registeredCount = registerModels(modelMap, providerMap);
            
            logger.info("模型注册表初始化完成，共注册 {} 个模型", registeredCount);
        } catch (Exception e) {
            logger.error("初始化模型注册表失败", e);
            throw new YonchainException("模型注册表初始化失败: " + e.getMessage(), e);
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
            
            // 提取配置信息
            provider.getConfigSchemas().forEach(configItem -> {
                if ("apiKey".equals(configItem.getName())) {
                    providerEntity.setApiKey((String) configItem.getValue());
                } else if ("baseUrl".equals(configItem.getName())) {
                    providerEntity.setBaseUrl((String) configItem.getValue());
                }
            });
            
            providerMap.put(provider.getCode(), providerEntity);
        }
        return providerMap;
    }
    
    /**
     * 注册模型到注册表
     * @return 成功注册的模型数量
     */
    private int registerModels(Map<String, ModelEntity> modelMap, Map<String, ModelProviderEntity> providerMap) {
        int registeredCount = 0;
        
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
                throw new YonchainException("模型 " + model.getModelCode() + " 的提供商 " + providerCode + " 不存在");
            }
            
            if (provider.getEnabled() == null || !provider.getEnabled()) {
               //throw new YonchainException("模型 " + model.getModelCode() + " 的提供商 " + providerCode + " 未启用");
            }
            
            // 注册模型
            String modelId = model.getModelCode() + "-" + provider.getProviderCode();
            try {
                ChatModel chatModel = modelFactory.getChatModel(model, provider);
                
                if (chatModel != null) {
                    modelRegistry.registerModel(modelId, chatModel);
                    logger.info("成功注册模型: {}", modelId);
                    registeredCount++;
                } else {
                    logger.warn("无法创建模型 {}, 工厂返回null", modelId);
                }
            } catch (Exception e) {
                logger.error("注册模型失败: {}, 错误: {}", modelId, e.getMessage(), e);
                // 继续注册其他模型，不中断流程
            }
        }
        
        return registeredCount;
    }
}
