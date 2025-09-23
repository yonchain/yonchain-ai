package com.yonchain.ai.example;

import com.yonchain.ai.model.core.ModelClient;
import com.yonchain.ai.model.core.ModelClientFactory;
import com.yonchain.ai.model.core.ModelClientFactoryBuilder;
import com.yonchain.ai.model.request.ChatRequest;
import com.yonchain.ai.model.request.ImageRequest;
import com.yonchain.ai.model.request.EmbeddingRequest;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.image.ImageResponse;
import org.springframework.ai.embedding.EmbeddingResponse;
import reactor.core.publisher.Flux;

/**
 * 新架构使用示例
 * 
 * 演示完全替换后的模型管理系统使用方式
 */
public class NewArchitectureExample {
    
    public static void main(String[] args) {
        // 1. 构建ModelClient（MyBatis风格）
        ModelClientFactory factory = new ModelClientFactoryBuilder()
            .build("model-config.xml");
        
        try (ModelClient client = factory.createClient()) {
            
            // 2. 使用命名空间模型调用
            namespaceModelExample(client);
            
            // 3. 使用别名调用
            aliasModelExample(client);
            
            // 4. 使用业务场景模型
            businessScenarioExample(client);
            
            // 5. 流式调用示例
            streamingExample(client);
            
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    /**
     * 命名空间模型调用示例
     */
    private static void namespaceModelExample(ModelClient client) {
        System.out.println("=== 命名空间模型调用示例 ===");
        
        try {
            // OpenAI GPT-4
            ChatRequest gpt4Request = ChatRequest.builder()
                .message("解释一下什么是人工智能")
                .build();
            ChatResponse gpt4Response = client.chat("openai:gpt-4", gpt4Request);
            System.out.println("GPT-4: " + gpt4Response.getResult().getOutput().getText());
            
            // DeepSeek Chat
            ChatRequest deepseekRequest = ChatRequest.builder()
                .message("写一个Python函数计算斐波那契数列")
                .build();
            ChatResponse deepseekResponse = client.chat("deepseek:deepseek-chat", deepseekRequest);
            System.out.println("DeepSeek: " + deepseekResponse.getResult().getOutput().getText());
            
            // OpenAI 图像生成
            ImageRequest imageRequest = ImageRequest.builder()
                .prompt("A beautiful sunset over mountains")
                .size("1024x1024")
                .build();
            ImageResponse imageResponse = client.generateImage("openai:dall-e-3", imageRequest);
            System.out.println("DALL-E 3: " + imageResponse.getResult().getOutput().getUrl());
            
        } catch (Exception e) {
            System.err.println("命名空间模型调用失败: " + e.getMessage());
        }
    }
    
    /**
     * 别名调用示例
     */
    private static void aliasModelExample(ModelClient client) {
        System.out.println("\n=== 别名调用示例 ===");
        
        try {
            // 使用别名调用（配置中 default-chat = openai:gpt-4）
            ChatRequest request = ChatRequest.builder()
                .message("什么是区块链技术？")
                .build();
            ChatResponse response = client.chat("default-chat", request);
            System.out.println("Default Chat (GPT-4): " + response.getResult().getOutput().getText());
            
            // 使用fast-chat别名（配置中 fast-chat = deepseek:deepseek-chat）
            ChatResponse fastResponse = client.chat("fast-chat", request);
            System.out.println("Fast Chat (DeepSeek): " + fastResponse.getResult().getOutput().getText());
            
        } catch (Exception e) {
            System.err.println("别名调用失败: " + e.getMessage());
        }
    }
    
    /**
     * 业务场景模型示例
     */
    private static void businessScenarioExample(ModelClient client) {
        System.out.println("\n=== 业务场景模型示例 ===");
        
        try {
            // 客服场景
            ChatRequest customerServiceRequest = ChatRequest.builder()
                .message("我的订单还没有收到，请帮我查一下")
                .build();
            ChatResponse customerResponse = client.chat("business:customer-service", customerServiceRequest);
            System.out.println("客服助手: " + customerResponse.getResult().getOutput().getText());
            
            // 代码审查场景
            ChatRequest codeReviewRequest = ChatRequest.builder()
                .message("""
                请审查这段代码：
                ```java
                public String getName() {
                    return name;
                }
                ```
                """)
                .build();
            ChatResponse codeResponse = client.chat("business:code-reviewer", codeReviewRequest);
            System.out.println("代码审查: " + codeResponse.getResult().getOutput().getText());
            
            // 内容创作场景
            ChatRequest contentRequest = ChatRequest.builder()
                .message("写一篇关于AI发展趋势的文章开头")
                .build();
            ChatResponse contentResponse = client.chat("business:content-writer", contentRequest);
            System.out.println("内容创作: " + contentResponse.getResult().getOutput().getText());
            
        } catch (Exception e) {
            System.err.println("业务场景调用失败: " + e.getMessage());
        }
    }
    
    /**
     * 流式调用示例
     */
    private static void streamingExample(ModelClient client) {
        System.out.println("\n=== 流式调用示例 ===");
        
        try {
            ChatRequest streamRequest = ChatRequest.builder()
                .message("请详细解释机器学习的基本概念")
                .build();
            
            System.out.print("流式响应: ");
            Flux<ChatResponse> responseStream = client.chatStream("openai:gpt-4", streamRequest);
            
            responseStream.subscribe(
                response -> {
                    String content = response.getResult().getOutput().getText();
                    System.out.print(content);
                },
                error -> System.err.println("\n流式调用错误: " + error.getMessage()),
                () -> System.out.println("\n流式调用完成")
            );
            
            // 等待流式响应完成
            Thread.sleep(10000);
            
        } catch (Exception e) {
            System.err.println("流式调用失败: " + e.getMessage());
        }
    }
    
    /**
     * 嵌入模型示例
     */
    private static void embeddingExample(ModelClient client) {
        System.out.println("\n=== 嵌入模型示例 ===");
        
        try {
            EmbeddingRequest embeddingRequest = EmbeddingRequest.builder()
                .text("这是一个测试文本，用于生成向量嵌入")
                .build();
            
            EmbeddingResponse embeddingResponse = client.embedding("openai:text-embedding-ada-002", embeddingRequest);
            System.out.println("嵌入向量长度: " + embeddingResponse.getResult().getOutput().length);
            System.out.println("前5个维度: ");
            float[] embedding = embeddingResponse.getResult().getOutput();
            for (int i = 0; i < Math.min(5, embedding.length); i++) {
                System.out.printf("  [%d]: %.6f%n", i, embedding[i]);
            }
            
        } catch (Exception e) {
            System.err.println("嵌入模型调用失败: " + e.getMessage());
        }
    }
}
