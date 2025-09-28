# 插件系统ModelFactory优化方案

## 🎯 问题分析

### 当前架构问题

1. **接口职责混乱**
   - `ModelFactory.createChatModel(ModelDefinition definition)`
   - `ModelProvider.createChatModel(ModelConfig config)`
   - 参数类型不一致，职责重叠

2. **双重注册机制复杂**
   - 插件提供商注册到DefaultModelFactory
   - DefaultModelFactory作为整体注册到ModelConfiguration
   - 管理链路过长

3. **DefaultModelFactory职责过重**
   - 既实现ModelFactory接口
   - 又管理所有ModelProvider
   - 承担适配器职责

## 🚀 优化方案

### 方案A: 统一工厂接口 (推荐)

#### 1. 统一参数类型
```java
// 修改ModelFactory接口，使用统一参数
public interface ModelFactory {
    boolean supports(ModelType modelType);
    
    // 统一使用ModelDefinition作为参数
    default ChatModel createChatModel(ModelDefinition definition) {
        throw new UnsupportedOperationException("Chat model not supported");
    }
    
    default ImageModel createImageModel(ModelDefinition definition) {
        throw new UnsupportedOperationException("Image model not supported");
    }
    
    default EmbeddingModel createEmbeddingModel(ModelDefinition definition) {
        throw new UnsupportedOperationException("Embedding model not supported");
    }
}
```

#### 2. 插件提供商适配器
```java
/**
 * 将ModelProvider适配为ModelFactory
 */
public class ProviderToFactoryAdapter implements ModelFactory {
    
    private final ModelProvider modelProvider;
    private final String providerName;
    
    public ProviderToFactoryAdapter(ModelProvider modelProvider) {
        this.modelProvider = modelProvider;
        this.providerName = modelProvider.getProviderName();
    }
    
    @Override
    public boolean supports(ModelType modelType) {
        return modelProvider.supports(modelType);
    }
    
    @Override
    public ChatModel createChatModel(ModelDefinition definition) {
        if (!supports(ModelType.CHAT)) {
            throw new UnsupportedOperationException("Chat model not supported by " + providerName);
        }
        
        // 将ModelDefinition转换为ModelConfig
        ModelConfig config = convertToModelConfig(definition);
        return modelProvider.createChatModel(config);
    }
    
    @Override
    public ImageModel createImageModel(ModelDefinition definition) {
        if (!supports(ModelType.IMAGE)) {
            throw new UnsupportedOperationException("Image model not supported by " + providerName);
        }
        
        ModelConfig config = convertToModelConfig(definition);
        return modelProvider.createImageModel(config);
    }
    
    @Override
    public EmbeddingModel createEmbeddingModel(ModelDefinition definition) {
        if (!supports(ModelType.EMBEDDING)) {
            throw new UnsupportedOperationException("Embedding model not supported by " + providerName);
        }
        
        ModelConfig config = convertToModelConfig(definition);
        return modelProvider.createEmbeddingModel(config);
    }
    
    /**
     * 将ModelDefinition转换为ModelConfig
     */
    private ModelConfig convertToModelConfig(ModelDefinition definition) {
        ModelConfig config = new ModelConfig();
        
        // 基本信息
        config.setName(definition.getId());
        config.setProvider(definition.getNamespace());
        config.setType(convertModelType(definition.getType()));
        
        // 认证信息
        config.setApiKey(definition.getAuthValue());
        config.setEndpoint(definition.getBaseUrl());
        
        // 路径信息
        if (definition.getCompletionsPath() != null) {
            config.setProperty("completionsPath", definition.getCompletionsPath());
        }
        
        // 选项信息 - 使用OptionsHandler构建
        if (definition.hasOptionsHandler(definition.getModelConfiguration())) {
            try {
                ModelOptionsHandler<?> handler = definition.resolveOptionsHandler();
                if (handler != null && definition.getOptions() != null) {
                    // 将选项Map转换为具体的Options对象
                    Object options = handler.buildOptions(definition.getOptions());
                    config.setProperty("springAiOptions", options);
                }
            } catch (Exception e) {
                log.warn("Failed to build options for model: {}", definition.getFullId(), e);
            }
        }
        
        return config;
    }
    
    private com.yonchain.ai.tmpl.ModelType convertModelType(String type) {
        switch (type.toLowerCase()) {
            case "chat": return com.yonchain.ai.tmpl.ModelType.TEXT;
            case "image": return com.yonchain.ai.tmpl.ModelType.IMAGE;
            case "embedding": return com.yonchain.ai.tmpl.ModelType.EMBEDDING;
            default: return com.yonchain.ai.tmpl.ModelType.TEXT;
        }
    }
}
```

#### 3. 简化ModelPluginAdapter
```java
@Override
public void onPluginEnable(String pluginId) throws PluginException {
    // ... 前面的步骤保持不变
    
    // 4. 获取模型提供商
    ModelProvider modelProvider = pluginInstance.getProvider();
    
    // 5. 创建适配器并直接注册为ModelFactory
    ProviderToFactoryAdapter factoryAdapter = new ProviderToFactoryAdapter(modelProvider);
    modelConfiguration.registerFactory(modelProvider.getProviderName(), factoryAdapter);
    
    // 6. 注册OptionsHandlers
    registerPluginOptionsHandlers(pluginInstance, modelConfiguration);
    
    // ... 后续步骤
}
```

### 方案B: 保持现状，优化适配 (兼容性好)

#### 1. 增强DefaultModelFactory
```java
@Service
public class EnhancedDefaultModelFactory implements PluginModelFactory {
    
    private final Map<String, ModelProvider> providers = new ConcurrentHashMap<>();
    private final Map<String, ProviderToFactoryAdapter> adapters = new ConcurrentHashMap<>();
    
    @Override
    public ChatModel createChatModel(ModelDefinition definition) {
        String providerName = definition.getNamespace();
        ProviderToFactoryAdapter adapter = getOrCreateAdapter(providerName);
        return adapter.createChatModel(definition);
    }
    
    @Override
    public void registerProvider(String providerName, ModelProvider modelProvider) {
        providers.put(providerName, modelProvider);
        // 创建适配器缓存
        adapters.put(providerName, new ProviderToFactoryAdapter(modelProvider));
        logger.info("Registered model provider with adapter: {}", providerName);
    }
    
    private ProviderToFactoryAdapter getOrCreateAdapter(String providerName) {
        ProviderToFactoryAdapter adapter = adapters.get(providerName);
        if (adapter == null) {
            ModelProvider provider = providers.get(providerName);
            if (provider == null) {
                throw new IllegalArgumentException("Unknown provider: " + providerName);
            }
            adapter = new ProviderToFactoryAdapter(provider);
            adapters.put(providerName, adapter);
        }
        return adapter;
    }
}
```

#### 2. 简化注册逻辑
```java
// 在ModelPluginAdapter中，只需要一次注册
@Override
public void onPluginEnable(String pluginId) throws PluginException {
    // ... 前面步骤
    
    // 统一注册：插件提供商注册到增强工厂，工厂自动处理适配
    modelFactory.registerProvider(modelProvider.getProviderName(), modelProvider);
    
    // 如果是第一次注册工厂，注册到ModelConfiguration
    if (!modelConfiguration.containsFactory(modelProvider.getProviderName())) {
        modelConfiguration.registerFactory(modelProvider.getProviderName(), modelFactory);
    }
    
    // ... 后续步骤
}
```

## 📊 方案对比

| 方案 | 优点 | 缺点 | 推荐度 |
|------|------|------|--------|
| **方案A** | 架构清晰、职责单一、直接映射 | 需要修改现有接口 | ⭐⭐⭐⭐⭐ |
| **方案B** | 兼容性好、改动最小 | 仍然存在职责混乱 | ⭐⭐⭐ |

## 🎯 推荐实施路径

### 阶段1: 创建适配器 (立即可行)
1. 创建`ProviderToFactoryAdapter`类
2. 在`DefaultModelFactory`中使用适配器
3. 验证功能正常

### 阶段2: 接口统一 (中期重构)
1. 统一`ModelFactory`和`ModelProvider`的参数类型
2. 简化注册机制
3. 清理冗余代码

### 阶段3: 架构优化 (长期规划)
1. 考虑引入工厂注册中心
2. 实现插件热插拔
3. 添加工厂监控和管理

## 🔧 实施建议

### 立即行动
```java
// 1. 创建适配器解决当前问题
ProviderToFactoryAdapter adapter = new ProviderToFactoryAdapter(modelProvider);
modelConfiguration.registerFactory(providerName, adapter);

// 2. 移除冗余的双重注册
// modelFactory.registerProvider(...); // 删除这行
```

### 中期优化
```java
// 统一接口参数类型
public interface ModelFactory {
    ChatModel createChatModel(ModelDefinition definition);
    // ... 其他方法也使用ModelDefinition
}
```

### 长期规划
- 考虑实现插件工厂的动态加载和卸载
- 添加工厂性能监控
- 实现工厂故障转移机制

