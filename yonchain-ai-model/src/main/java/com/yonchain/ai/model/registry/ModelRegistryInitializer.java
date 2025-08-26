package com.yonchain.ai.model.registry;

import com.yonchain.ai.api.exception.YonchainException;
import com.yonchain.ai.model.loader.ModelLoader;
import com.yonchain.ai.model.mapper.ModelMapper;
import com.yonchain.ai.model.mapper.ModelProviderMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;


/**
 * 模型注册表初始化器
 * <p>
 * 负责在应用启动时加载模型和提供商配置信息到注册表
 * 不负责模型实例的创建和管理，只负责加载静态信息到注册表
 *
 * @author Cgy
 */
public class ModelRegistryInitializer implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(ModelRegistryInitializer.class);

    private final ModelRegistry modelRegistry;

    private final ModelLoader modelLoader;

    private final ModelProviderMapper modelProviderMapper;

    private final ModelMapper modelMapper;

    public ModelRegistryInitializer(ModelRegistry modelRegistry,
                                    ModelLoader modelLoader,
                                    ModelProviderMapper modelProviderMapper,
                                    ModelMapper modelMapper) {
        this.modelLoader = modelLoader;
        this.modelRegistry = modelRegistry;
        this.modelProviderMapper = modelProviderMapper;
        this.modelMapper = modelMapper;
    }

    @Override
    public void run(String... args) {
        log.info("开始初始化模型注册表...");

        try {
            // 加载提供商配置
            modelRegistry.registerProviders(modelLoader.loadProviders());

            // 加载模型配置
            modelRegistry.registerModels(modelLoader.loadModels());

            log.info("模型注册表初始化完成");

        } catch (Exception e) {
            log.error("模型注册表初始化失败", e);
            throw new YonchainException("模型注册表初始化失败", e);
        }
    }

}