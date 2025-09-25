package com.yonchain.ai.model;

import com.yonchain.ai.api.exception.YonchainException;
import com.yonchain.ai.model.definition.ModelDefinition;
import com.yonchain.ai.model.factory.ModelFactory;
import com.yonchain.ai.model.options.ModelOptionsHandler;
import com.yonchain.ai.model.request.ChatRequest;
import com.yonchain.ai.model.request.EmbeddingRequest;
import com.yonchain.ai.model.request.ImageRequest;
import com.yonchain.ai.model.util.ModelIdParser;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.prompt.ChatOptions;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.embedding.EmbeddingModel;
import org.springframework.ai.embedding.EmbeddingResponse;
import org.springframework.ai.image.ImageModel;
import org.springframework.ai.image.ImageResponse;
import org.springframework.ai.model.Model;
import reactor.core.publisher.Flux;

import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 默认的ModelClient实现
 */
public class DefaultModelClient implements ModelClient {
    
    private final ModelConfiguration configuration;
    
    // 模型实例缓存
    private final ConcurrentHashMap<String, Model<?, ?>> modelCache = new ConcurrentHashMap<>();
    
    public DefaultModelClient(ModelConfiguration configuration) {
        this.configuration = configuration;
    }
    
    @Override
    public ChatResponse chat(String modelId, ChatRequest request) {
        ChatModel chatModel = getChatModel(modelId);
        
        // 处理运行时模型选项
        ChatOptions runtimeOptions = buildRuntimeOptions(modelId, request);

        Prompt prompt = request.toPrompt(runtimeOptions);

        return chatModel.call(prompt);
    }
    
    @Override
    public Flux<ChatResponse> chatStream(String modelId, ChatRequest request) {
        ChatModel chatModel = getChatModel(modelId);
        
        // 处理运行时模型选项
        ChatOptions runtimeOptions = buildRuntimeOptions(modelId, request);

        Prompt prompt = request.toPrompt(runtimeOptions);

        return chatModel.stream(prompt);
    }
    
    @Override
    public ImageResponse generateImage(String modelId, ImageRequest request) {
        ImageModel imageModel = getImageModel(modelId);
        return imageModel.call(request.toImagePrompt());
    }
    
    @Override
    public EmbeddingResponse embedding(String modelId, EmbeddingRequest request) {
        EmbeddingModel embeddingModel = getEmbeddingModel(modelId);
        return embeddingModel.call(request.toEmbeddingRequest());
    }
    
    @Override
    public boolean isModelAvailable(String modelId) {
        try {
            ModelDefinition definition = resolveModelDefinition(modelId);
            return definition != null;
        } catch (Exception e) {
            return false;
        }
    }
    
    @Override
    public ModelConfiguration getConfiguration() {
        return configuration;
    }
    
    @Override
    public void close() throws Exception {
        // 清理缓存的模型实例
        modelCache.clear();
    }
    
    private ChatModel getChatModel(String modelId) {
        return (ChatModel) modelCache.computeIfAbsent(modelId + ":chat", k -> {
            ModelDefinition definition = resolveModelDefinition(modelId);
            
            // 设置ModelConfiguration到ModelDefinition，让Factory内部处理
            definition.setModelConfiguration(configuration);
            
            // 使用命名空间工厂
            ModelFactory factory = getModelFactory(definition.getNamespace());
            return factory.createChatModel(definition);
        });
    }
    
    private ImageModel getImageModel(String modelId) {
        return (ImageModel) modelCache.computeIfAbsent(modelId + ":image", k -> {
            ModelDefinition definition = resolveModelDefinition(modelId);
            
            // 设置ModelConfiguration到ModelDefinition，让Factory内部处理
            definition.setModelConfiguration(configuration);
            
            // 使用命名空间工厂
            ModelFactory factory = getModelFactory(definition.getNamespace());
            return factory.createImageModel(definition);
        });
    }
    
    private EmbeddingModel getEmbeddingModel(String modelId) {
        return (EmbeddingModel) modelCache.computeIfAbsent(modelId + ":embedding", k -> {
            ModelDefinition definition = resolveModelDefinition(modelId);
            
            // 设置ModelConfiguration到ModelDefinition，让Factory内部处理
            definition.setModelConfiguration(configuration);
            
            // 使用命名空间工厂
            ModelFactory factory = getModelFactory(definition.getNamespace());
            return factory.createEmbeddingModel(definition);
        });
    }
    
    private ModelDefinition resolveModelDefinition(String modelId) {
        ModelIdParser.ParsedModelId parsed = ModelIdParser.parse(modelId);
        
        Optional<ModelDefinition> definition = configuration.getModelRegistry()
            .getModelDefinition(parsed.getNamespace(), parsed.getModelName());
            
        return definition.orElseThrow(() -> 
            new IllegalArgumentException("Model not found: " + modelId));
    }
    
    private ModelFactory getModelFactory(String namespace) {
        return configuration.getModelFactoryRegistry()
            .getFactory(namespace)
            .orElseThrow(() -> 
                new IllegalArgumentException("No factory found for namespace: " + namespace));
    }
    
    /**
     * 构建运行时模型选项
     * 
     * 根据请求中的原始参数和模型配置，动态构建适合当前模型的选项
     * 
     * @param modelId 模型ID
     * @param request 请求对象
     * @return 运行时模型选项，如果无法构建则返回null
     */
    @SuppressWarnings("unchecked")
    private <T extends ChatOptions> T buildRuntimeOptions(String modelId, ChatRequest request) {
        // 如果没有原始参数，直接返回null
        if (request.getRawParameters() == null || request.getRawParameters().isEmpty()) {
            return null;
        }
        
        try {
            // 解析模型定义
            ModelDefinition definition = resolveModelDefinition(modelId);
            
            // 获取OptionsHandler
            ModelOptionsHandler<T> handler =
                configuration.getOptionsHandlerRegistry().resolveHandler(
                    definition.getNamespace(), 
                    definition.getId(), 
                    definition.getType(), 
                    definition.getOptionsHandler()
                );
            
            if (handler != null) {
                // 使用Handler构建运行时选项
                T options = handler.buildOptions(request.getRawParameters());
                System.out.println("DEBUG: Built runtime options for " + modelId + " using " + handler.getClass().getSimpleName());
                return options;
            } else {
                System.out.println("DEBUG: No OptionsHandler found for " + modelId + ", using default options");
                throw new YonchainException("No OptionsHandler found for " + modelId + ", using default options");
            }
            
        } catch (Exception e) {
            System.err.println("ERROR: Failed to build runtime options for " + modelId + ": " + e.getMessage());
           throw new YonchainException("ERROR: Failed to build runtime options for " + modelId + ": " + e.getMessage(),e);
        }
    }
}
