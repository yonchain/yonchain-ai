package com.yonchain.ai.model;

import com.yonchain.ai.model.request.ChatRequest;
import com.yonchain.ai.model.request.EmbeddingRequest;
import com.yonchain.ai.model.request.ImageRequest;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.embedding.EmbeddingResponse;
import org.springframework.ai.image.ImageResponse;
import reactor.core.publisher.Flux;

/**
 * Core model client entry-point.
 * <p>
 * Responsibilities:
 * - Resolve model definition by modelId (namespace:id)
 * - Route to model-level factory (if present) or namespace factory
 * - Obtain Spring AI Model and invoke directly
 */
public interface ModelClient {

    // Chat
    ChatResponse chat(String modelId, ChatRequest request);

    Flux<ChatResponse> chatStream(String modelId, ChatRequest request);

    // Image
    ImageResponse generateImage(String modelId, ImageRequest request);

    // Embedding
    EmbeddingResponse embedding(String modelId, EmbeddingRequest request);

    // Get model configuration
    ModelConfiguration getConfiguration();

}


