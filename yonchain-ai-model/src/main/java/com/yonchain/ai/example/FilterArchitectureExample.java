package com.yonchain.ai.example;

/**
 * 新过滤器架构使用示例
 * 
 * 展示按模型类型分离的过滤器架构
 */
public class FilterArchitectureExample {
    
    /**
     * 这个示例展示了不同类型的API请求如何被相应的过滤器处理
     */
    public static void demonstrateFilterRouting() {
        
        System.out.println("=== 过滤器路由示例 ===\n");
        
        // 1. 聊天请求 -> ChatModelFilter
        System.out.println("1. 聊天请求处理:");
        System.out.println("   POST /v1/chat/completions");
        System.out.println("   {\"model\": \"openai:gpt-4\", \"messages\": [...]}");
        System.out.println("   → ChatModelFilter 处理");
        System.out.println("   → 支持同步和流式响应");
        System.out.println("   → 转换为OpenAI聊天格式\n");
        
        // 2. 图像请求 -> ImageModelFilter  
        System.out.println("2. 图像生成请求处理:");
        System.out.println("   POST /v1/images/generations");
        System.out.println("   {\"model\": \"openai:dall-e-3\", \"prompt\": \"A sunset\"}");
        System.out.println("   → ImageModelFilter 处理");
        System.out.println("   → 支持多种尺寸和质量");
        System.out.println("   → 返回图像URL或base64数据\n");
        
        // 3. 嵌入请求 -> EmbeddingModelFilter
        System.out.println("3. 嵌入请求处理:");
        System.out.println("   POST /v1/embeddings");
        System.out.println("   {\"model\": \"openai:text-embedding-ada-002\", \"input\": \"text\"}");
        System.out.println("   → EmbeddingModelFilter 处理");
        System.out.println("   → 支持单个文本或文本数组");
        System.out.println("   → 返回向量嵌入数据\n");
        
        // 4. 音频请求 -> AudioModelFilter
        System.out.println("4. 音频请求处理:");
        System.out.println("   POST /v1/audio/transcriptions");
        System.out.println("   (multipart/form-data with audio file)");
        System.out.println("   → AudioModelFilter 处理");
        System.out.println("   → 语音转文字功能");
        System.out.println("   → 支持多种音频格式\n");
        
        System.out.println("   POST /v1/audio/speech");
        System.out.println("   {\"model\": \"tts-1\", \"input\": \"Hello\", \"voice\": \"alloy\"}");
        System.out.println("   → AudioModelFilter 处理");
        System.out.println("   → 文字转语音功能");
        System.out.println("   → 返回音频数据\n");
    }
    
    /**
     * 展示过滤器架构的优势
     */
    public static void demonstrateArchitectureAdvantages() {
        
        System.out.println("=== 过滤器架构优势 ===\n");
        
        System.out.println("1. 职责分离:");
        System.out.println("   - ChatModelFilter: 专门处理聊天逻辑");
        System.out.println("   - ImageModelFilter: 专门处理图像生成逻辑");
        System.out.println("   - EmbeddingModelFilter: 专门处理嵌入逻辑");
        System.out.println("   - AudioModelFilter: 专门处理音频逻辑\n");
        
        System.out.println("2. 易于维护:");
        System.out.println("   - 每个过滤器只关注特定的模型类型");
        System.out.println("   - 修改某种模型的处理逻辑不影响其他模型");
        System.out.println("   - 代码结构清晰，易于理解\n");
        
        System.out.println("3. 高度可扩展:");
        System.out.println("   - 添加新的模型类型只需要新增对应的过滤器");
        System.out.println("   - 每个过滤器可以独立优化性能");
        System.out.println("   - 支持不同的请求格式和响应格式\n");
        
        System.out.println("4. 统一的ModelClient:");
        System.out.println("   - 所有过滤器共享同一个ModelClient实例");
        System.out.println("   - 统一的命名空间和别名解析");
        System.out.println("   - 统一的配置管理和工厂创建\n");
    }
    
    /**
     * 展示配置驱动的路由
     */
    public static void demonstrateConfigDrivenRouting() {
        
        System.out.println("=== 配置驱动路由示例 ===\n");
        
        System.out.println("XML配置文件定义:");
        System.out.println("```xml");
        System.out.println("<!-- models/openai.xml -->");
        System.out.println("<models namespace=\"openai\">");
        System.out.println("  <model id=\"gpt-4\" type=\"chat\">");
        System.out.println("    <endpoint>https://api.openai.com/v1/chat/completions</endpoint>");
        System.out.println("    <auth type=\"bearer\">${openai.apiKey}</auth>");
        System.out.println("  </model>");
        System.out.println("  <model id=\"dall-e-3\" type=\"image\">");
        System.out.println("    <endpoint>https://api.openai.com/v1/images/generations</endpoint>");
        System.out.println("    <auth type=\"bearer\">${openai.apiKey}</auth>");
        System.out.println("  </model>");
        System.out.println("</models>");
        System.out.println("```\n");
        
        System.out.println("路由逻辑:");
        System.out.println("1. 请求 'openai:gpt-4' → type='chat' → ChatModelFilter");
        System.out.println("2. 请求 'openai:dall-e-3' → type='image' → ImageModelFilter");
        System.out.println("3. 过滤器根据配置创建对应的Spring AI Model");
        System.out.println("4. 统一的OpenAI格式响应转换\n");
    }
    
    public static void main(String[] args) {
        demonstrateFilterRouting();
        System.out.println();
        demonstrateArchitectureAdvantages();
        System.out.println();
        demonstrateConfigDrivenRouting();
    }
}

