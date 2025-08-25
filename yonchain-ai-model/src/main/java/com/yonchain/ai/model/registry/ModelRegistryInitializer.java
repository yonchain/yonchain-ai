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
            Map<String, ModelEntity> modelMap = new HashMap<>();
            for (ModelInfo model : models) {
                ModelEntity modelEntity = new ModelEntity();
                modelEntity.setModelCode(model.getCode());
                // ModelEntity中没有setModelName等方法，只使用已有的属性
                
                // 设置是否启用
                modelEntity.setEnabled(model.getEnabled());
                
                modelMap.put(model.getCode(), modelEntity);
            }
            
            // 将提供商信息转换为ModelProviderEntity
            Map<String, ModelProviderEntity> providerMap = new HashMap<>();
            for (ModelProvider provider : providers) {
                ModelProviderEntity providerEntity = new ModelProviderEntity();
                providerEntity.setProviderCode(provider.getCode());
                // ModelProviderEntity中没有setProviderName等方法，只使用已有的属性
                
                // 设置API密钥和基础URL
                providerEntity.setApiKey(provider.getCode()); // 临时使用code作为apiKey
                providerEntity.setBaseUrl(""); // 默认为空
                
                // 设置是否启用
                providerEntity.setEnabled(provider.getEnabled());
                
                providerMap.put(provider.getCode(), providerEntity);
            }
            
            // 为每个启用的模型和提供商组合创建ChatModel并注册到注册表
            for (ModelEntity model : modelMap.values()) {
                /*if (model.getEnabled() == null || !model.getEnabled()) {
                    continue;
                }*/
                
                for (ModelProviderEntity provider : providerMap.values()) {
                   /* if (provider.getEnabled() == null || !provider.getEnabled()) {
                        continue;
                    }*/
                    
                    try {
                        // 创建ChatModel
                        ChatModel chatModel = modelFactory.getChatModel(model, provider);
                        
                        if (chatModel != null) {
                            // 注册到注册表
                            String modelId = model.getModelCode() + "-" + provider.getProviderCode();
                            modelRegistry.registerModel(modelId, chatModel);
                            logger.info("成功注册模型: {}", modelId);
                        }else {
                            throw new YonchainException("注册失败");
                        }
                    } catch (Exception e) {
                        logger.error("注册模型失败: {} - {}, 错误: {}", model.getModelCode(), provider.getProviderCode(), e.getMessage());
                    }
                }
            }
            
            logger.info("模型注册表初始化完成，共注册 {} 个模型", modelRegistry.getModelCount());
        } catch (Exception e) {
            logger.error("初始化模型注册表失败", e);
        }
    }
}
