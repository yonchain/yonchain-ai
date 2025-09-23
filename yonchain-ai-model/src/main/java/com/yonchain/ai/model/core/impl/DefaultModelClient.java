package com.yonchain.ai.model.core.impl;

import com.yonchain.ai.model.core.ModelClient;
import com.yonchain.ai.model.core.ModelConfiguration;
import com.yonchain.ai.model.definition.ModelDefinition;
import com.yonchain.ai.model.factory.ModelFactory;
import com.yonchain.ai.model.request.ChatRequest;
import com.yonchain.ai.model.request.EmbeddingRequest;
import com.yonchain.ai.model.request.ImageRequest;
import com.yonchain.ai.model.util.ModelIdParser;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.embedding.EmbeddingModel;
import org.springframework.ai.embedding.EmbeddingResponse;
import org.springframework.ai.image.ImageModel;
import org.springframework.ai.image.ImageResponse;
import reactor.core.publisher.Flux;

import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 默认的ModelClient实现
 */
public class DefaultModelClient implements ModelClient {
    
    private final ModelConfiguration configuration;
    private final String defaultNamespace;
    
    // 模型实例缓存
    private final ConcurrentHashMap<String, Object> modelCache = new ConcurrentHashMap<>();
    
    public DefaultModelClient(ModelConfiguration configuration) {
        this(configuration, null);
    }
    
    public DefaultModelClient(ModelConfiguration configuration, String defaultNamespace) {
        this.configuration = configuration;
        this.defaultNamespace = defaultNamespace;
    }
    
    @Override
    public ChatResponse chat(String modelId, ChatRequest request) {
        ChatModel chatModel = getChatModel(modelId);
        return chatModel.call(request.toPrompt());
    }
    
    @Override
    public Flux<ChatResponse> chatStream(String modelId, ChatRequest request) {
        ChatModel chatModel = getChatModel(modelId);
        return chatModel.stream(request.toPrompt());
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
    public void close() throws Exception {
        // 清理缓存的模型实例
        modelCache.clear();
    }
    
    private ChatModel getChatModel(String modelId) {
        return (ChatModel) modelCache.computeIfAbsent(modelId + ":chat", k -> {
            ModelDefinition definition = resolveModelDefinition(modelId);
            
            // 使用命名空间工厂
            ModelFactory factory = getModelFactory(definition.getNamespace());
            return factory.createChatModel(definition, configuration.getTypeHandlerRegistry());
        });
    }
    
    private ImageModel getImageModel(String modelId) {
        return (ImageModel) modelCache.computeIfAbsent(modelId + ":image", k -> {
            ModelDefinition definition = resolveModelDefinition(modelId);
            
            // 使用命名空间工厂
            ModelFactory factory = getModelFactory(definition.getNamespace());
            return factory.createImageModel(definition, configuration.getTypeHandlerRegistry());
        });
    }
    
    private EmbeddingModel getEmbeddingModel(String modelId) {
        return (EmbeddingModel) modelCache.computeIfAbsent(modelId + ":embedding", k -> {
            ModelDefinition definition = resolveModelDefinition(modelId);
            
            // 使用命名空间工厂
            ModelFactory factory = getModelFactory(definition.getNamespace());
            return factory.createEmbeddingModel(definition, configuration.getTypeHandlerRegistry());
        });
    }
    
    private ModelDefinition resolveModelDefinition(String modelId) {
        ModelIdParser.ParsedModelId parsed = ModelIdParser.parse(modelId, defaultNamespace);
        
        Optional<ModelDefinition> definition = configuration.getModelRegistry()
            .getModelDefinition(parsed.getNamespace(), parsed.getModelName());
            
        return definition.orElseThrow(() -> 
            new IllegalArgumentException("Model not found: " + modelId));
    }
    
    private ModelFactory getModelFactory(String namespace) {
        return configuration.getNamespaceFactoryRegistry()
            .getFactory(namespace)
            .orElseThrow(() -> 
                new IllegalArgumentException("No factory found for namespace: " + namespace));
    }
}
