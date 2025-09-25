package com.yonchain.ai.model.example;

import com.yonchain.ai.model.ModelClient;
import com.yonchain.ai.model.ModelClientFactory;
import com.yonchain.ai.model.ModelClientFactoryBuilder;
import com.yonchain.ai.model.request.ChatRequest;
import com.yonchain.ai.model.request.ImageRequest;
import com.yonchain.ai.model.request.EmbeddingRequest;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.image.ImageResponse;
import org.springframework.ai.embedding.EmbeddingResponse;

/**
 * 模型客户端使用示例
 * 
 * 演示如何使用ModelClient进行各种AI模型调用
 */
public class ModelClientExample {
    
    public static void main(String[] args) {
        // 构建工厂
        ModelClientFactory factory = new ModelClientFactoryBuilder()
            .build("model-config.xml");
        
        // 创建客户端
        try (ModelClient client = factory.createClient()) {
            
            // 示例1: 调用聊天模型
            chatExample(client);
            
            // 示例2: 调用图像生成模型
            imageExample(client);
            
            // 示例3: 调用嵌入模型
            embeddingExample(client);
            
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    /**
     * 聊天模型使用示例
     */
    private static void chatExample(ModelClient client) {
        System.out.println("=== 聊天模型示例 ===");
        
        try {
            // 使用构建器创建请求
            ChatRequest request = ChatRequest.builder()
                .message("Hello, how are you?")
                .build();
            
            // 调用OpenAI GPT-4模型
            ChatResponse response = client.chat("openai:gpt-4", request);
            System.out.println("GPT-4 Response: " + response.getResult().getOutput());
            
            // 调用DeepSeek模型
            ChatResponse deepseekResponse = client.chat("deepseek:deepseek-chat", request);
            System.out.println("DeepSeek Response: " + deepseekResponse.getResult().getOutput());
            
        } catch (Exception e) {
            System.err.println("Chat example failed: " + e.getMessage());
        }
    }
    
    /**
     * 图像生成模型使用示例
     */
    private static void imageExample(ModelClient client) {
        System.out.println("\n=== 图像生成模型示例 ===");
        
        try {
            // 创建图像生成请求
            ImageRequest request = ImageRequest.builder()
                .prompt("A beautiful sunset over the ocean")
                .size("1024x1024")
                .build();
            
            // 调用DALL-E 3模型
            ImageResponse response = client.generateImage("openai:dall-e-3", request);
            System.out.println("Generated image URL: " + response.getResult().getOutput().getUrl());
            
        } catch (Exception e) {
            System.err.println("Image example failed: " + e.getMessage());
        }
    }
    
    /**
     * 嵌入模型使用示例
     */
    private static void embeddingExample(ModelClient client) {
        System.out.println("\n=== 嵌入模型示例 ===");
        
        try {
            // 创建嵌入请求
            EmbeddingRequest request = EmbeddingRequest.builder()
                .text("This is a test text for embedding")
                .build();
            
            // 调用OpenAI嵌入模型
            EmbeddingResponse response = client.embedding("openai:text-embedding-ada-002", request);
            System.out.println("Embedding vector length: " + response.getResult().getOutput().length);
            
        } catch (Exception e) {
            System.err.println("Embedding example failed: " + e.getMessage());
        }
    }
    
    /**
     * 业务场景使用示例
     */
    public static void businessScenarioExample() {
        ModelClientFactory factory = new ModelClientFactoryBuilder()
            .build("model-config.xml");
        
        try (ModelClient client = factory.createClient()) {
            
            // 使用业务场景配置的模型
            ChatRequest customerServiceRequest = ChatRequest.builder()
                .message("我的订单什么时候能到？")
                .build();
            
            // 调用客服聊天模型
            ChatResponse response = client.chat("business:customer-service-chat", customerServiceRequest);
            System.out.println("Customer Service Response: " + response.getResult().getOutput());
            
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
