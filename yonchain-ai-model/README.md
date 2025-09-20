# 永链AI模型管理模块

## 概述

yonchain-ai-model是永链AI系统的核心模块，负责模型的统一管理和调用。该模块遵循OpenAI API规范，基于Spring AI框架设计，提供了完整的企业级模型管理解决方案。

## 核心特性

- ✅ **OpenAI API兼容**：完全兼容OpenAI接口规范
- ✅ **Spring AI标准**：基于Spring AI框架设计
- ✅ **多模型支持**：文本、图像、音频、嵌入等多种模型类型
- ✅ **多提供商集成**：支持OpenAI、DeepSeek、Anthropic等主流提供商
- ✅ **提供商命名规范**：支持 `provider:model` 格式，避免命名冲突
- ✅ **分布式注册中心**：支持本地、Redis、Nacos等多种注册中心
- ✅ **职责分离**：清晰的分层架构，职责单一
- ✅ **配置驱动**：灵活的配置管理
- ✅ **企业级特性**：缓存、监控、异常处理

## 架构设计

### 核心组件

```
┌─────────────────────────────────────────────────────────┐
│                   过滤器层 (Filter Layer)                 │
│  ChatModelFilter  ImageModelFilter  AudioModelFilter    │
└─────────────────────────────────────────────────────────┘
                                │
┌─────────────────────────────────────────────────────────┐
│                  服务层 (Service Layer)                   │
│  ChatModelService  ImageModelService  AudioModelService │
│  • getModel(String) • 实例缓存 • 生命周期管理              │
└─────────────────────────────────────────────────────────┘
                                │
┌─────────────────────────────────────────────────────────┐
│                模型注册中心 (Model Registry)               │
│  LocalRegistry  RedisRegistry  NacosRegistry             │
└─────────────────────────────────────────────────────────┘
                                │
┌─────────────────────────────────────────────────────────┐
│                模型工厂层 (Model Factory)                  │
│  ModelFactory → ModelProvider → Spring AI实现            │
└─────────────────────────────────────────────────────────┘
```

### 请求处理流程

1. **请求接收**：过滤器接收OpenAI格式请求
2. **模型获取**：服务层通过注册中心获取模型元数据
3. **实例创建**：工厂根据配置创建模型实例
4. **模型调用**：调用Spring AI标准接口
5. **响应转换**：转换为OpenAI格式响应

## 快速开始

### 1. 添加依赖

```xml
<dependency>
    <groupId>com.yonchain.ai</groupId>
    <artifactId>yonchain-ai-model</artifactId>
    <version>0.1.0-SNAPSHOT</version>
</dependency>
```

### 2. 配置模型

创建 `application.yml` 配置文件：

```yaml
yonchain:
  ai:
    models:
      chat:
        - name: "deepseek-chat"      # 模型名称
          provider: "deepseek"       # 提供商名称  
          endpoint: "https://api.deepseek.com/v1"
          apiKey: "${DEEPSEEK_API_KEY}"
          maxTokens: 8192
          temperature: 0.7
          enabled: true
          # 系统中注册为: deepseek:deepseek-chat
```

> **重要说明**: 系统采用 `provider:model` 的命名格式来避免不同提供商的模型名称冲突。详见 [提供商模型命名规范](./PROVIDER_MODEL_NAMING.md)。

### 3. 使用示例

#### 程序调用
```java
@RestController
public class ChatController {
    
    @Autowired
    private ChatModelService chatModelService;
    
    @PostMapping("/chat/completions")
    public ResponseEntity<?> chat(@RequestBody Map<String, Object> request) {
        String modelName = (String) request.get("model");
        
        // 推荐：使用完整名称 provider:model
        ChatModel chatModel = chatModelService.getModel(modelName);
        
        // 构建提示
        Prompt prompt = new Prompt(new UserMessage("Hello"));
        
        // 调用模型
        ChatResponse response = chatModel.call(prompt);
        
        return ResponseEntity.ok(response);
    }
}
```

#### API调用
```bash
# 使用完整的模型名称（推荐）
curl -X POST http://localhost:8080/chat/completions \
  -H "Content-Type: application/json" \
  -d '{
    "model": "deepseek:deepseek-chat",
    "messages": [{"role": "user", "content": "Hello"}],
    "temperature": 0.7
  }'
  
# 向后兼容：简单名称也支持
curl -X POST http://localhost:8080/chat/completions \
  -H "Content-Type: application/json" \
  -d '{
    "model": "deepseek-chat",
    "messages": [{"role": "user", "content": "Hello"}]
  }'
```

## 配置详解

### 模型配置

```yaml
yonchain:
  ai:
    # 注册中心配置
    registry:
      type: local  # local, redis, nacos, hybrid
    
    # 模型配置
    models:
      # 聊天模型
      chat:
        - name: "模型名称"
          provider: "提供商名称"
          endpoint: "API端点"
          apiKey: "API密钥"
          maxTokens: 最大令牌数
          temperature: 温度参数
          enabled: true/false
          timeout: 超时时间(毫秒)
          retryCount: 重试次数
          properties:
            # 扩展属性
            model: "具体模型名"
            streaming: true/false
      
      # 图像模型
      image:
        - name: "dall-e-3"
          provider: "openai"
          # ... 其他配置
      
      # 音频模型
      audio:
        - name: "whisper-1"
          provider: "openai"
          # ... 其他配置
      
      # 嵌入模型
      embedding:
        - name: "text-embedding-3-small"
          provider: "openai"
          # ... 其他配置
```

### 注册中心配置

#### 本地注册中心（默认）
```yaml
yonchain:
  ai:
    registry:
      type: local
```

#### Redis注册中心
```yaml
yonchain:
  ai:
    registry:
      type: redis
      redis:
        host: localhost
        port: 6379
        database: 0
        password: 
```

#### Nacos注册中心
```yaml
yonchain:
  ai:
    registry:
      type: nacos
      nacos:
        server-addr: localhost:8848
        namespace: yonchain-ai
        group: DEFAULT_GROUP
```

## 扩展开发

### 添加新的模型提供商

1. **实现ModelProvider接口**

```java
@Component
public class CustomModelProvider implements ModelProvider {
    
    @Override
    public String getProviderName() {
        return "custom";
    }
    
    @Override
    public boolean supports(ModelType modelType) {
        return modelType == ModelType.TEXT;
    }
    
    @Override
    public ChatModel createChatModel(ModelConfig config) {
        return new CustomChatModel(config);
    }
    
    // ... 其他方法实现
}
```

2. **实现Spring AI模型接口**

```java
public class CustomChatModel implements ChatModel {
    
    @Override
    public ChatResponse call(Prompt prompt) {
        // 实现具体的模型调用逻辑
        return new ChatResponse(/* ... */);
    }
    
    @Override
    public Flux<ChatResponse> stream(Prompt prompt) {
        // 实现流式响应逻辑
        return Flux.create(/* ... */);
    }
}
```

3. **添加配置支持**

```yaml
yonchain:
  ai:
    models:
      chat:
        - name: "custom-model"
          provider: "custom"
          endpoint: "https://api.custom.com/v1"
          apiKey: "${CUSTOM_API_KEY}"
          enabled: true
```

### 添加新的注册中心实现

1. **实现ModelRegistry接口**

```java
@Component
@ConditionalOnProperty(name = "yonchain.ai.registry.type", havingValue = "consul")
public class ConsulModelRegistry implements ModelRegistry {
    
    @Override
    public void registerModel(ModelMetadata metadata) {
        // 实现Consul注册逻辑
    }
    
    @Override
    public Optional<ModelMetadata> getModelMetadata(String modelName) {
        // 实现Consul查询逻辑
        return Optional.empty();
    }
    
    // ... 其他方法实现
}
```

2. **添加自动配置**

```java
@Configuration
public class ConsulRegistryAutoConfiguration {
    
    @Bean
    @ConditionalOnProperty(name = "yonchain.ai.registry.type", havingValue = "consul")
    public ModelRegistry consulModelRegistry() {
        return new ConsulModelRegistry();
    }
}
```

## API接口

### OpenAI兼容接口

| 接口 | 路径 | 功能 | 状态 |
|------|------|------|------|
| 聊天接口 | `POST /v1/chat/completions` | 文本对话、流式响应 | ✅ |
| 图像接口 | `POST /v1/images/generations` | 图像生成 | ✅ |
| 音频接口 | `POST /v1/audio/transcriptions` | 语音转文字 | 🚧 |
| 嵌入接口 | `POST /v1/embeddings` | 文本向量化 | 🚧 |
| 模型接口 | `GET /v1/models` | 模型列表 | 🚧 |

### 请求示例

#### 聊天接口
```bash
curl -X POST http://localhost:8080/v1/chat/completions \
  -H "Content-Type: application/json" \
  -d '{
    "model": "deepseek:deepseek-chat",
    "messages": [
      {"role": "user", "content": "Hello, how are you?"}
    ],
    "temperature": 0.7,
    "max_tokens": 1000,
    "stream": false
  }'
```

#### 图像生成接口
```bash
curl -X POST http://localhost:8080/v1/images/generations \
  -H "Content-Type: application/json" \
  -d '{
    "model": "dall-e-3",
    "prompt": "A beautiful sunset over the mountains",
    "n": 1,
    "size": "1024x1024"
  }'
```

## 监控和运维

### 健康检查

系统提供了模型健康检查功能：

```java
@Autowired
private ChatModelService chatModelService;

// 检查模型是否可用
boolean isAvailable = chatModelService.isModelAvailable("deepseek-chat");

// 获取缓存的模型数量
int cachedCount = chatModelService.getCachedModelCount();

// 预热模型
chatModelService.warmupModel("deepseek-chat");
```

### 日志配置

```yaml
logging:
  level:
    com.yonchain.ai: DEBUG
    org.springframework.ai: DEBUG
  pattern:
    console: "%d{yyyy-MM-dd HH:mm:ss} [%thread] %-5level %logger{36} - %msg%n"
```

## 故障排除

### 常见问题

1. **模型未找到错误**
   - 检查配置文件中的模型名称
   - 确认模型已启用（enabled: true）
   - 查看日志确认模型是否成功注册

2. **API密钥错误**
   - 检查环境变量是否正确设置
   - 确认API密钥格式正确
   - 验证API密钥是否有效

3. **连接超时**
   - 检查网络连接
   - 调整timeout配置
   - 检查endpoint地址是否正确

### 调试模式

启用调试日志：

```yaml
logging:
  level:
    com.yonchain.ai: DEBUG
```

## 版本兼容性

- Spring Boot: 3.x
- Spring AI: 1.0.0
- Java: 17+

## 贡献指南

1. Fork项目
2. 创建功能分支
3. 提交更改
4. 创建Pull Request

## 许可证

Apache 2.0 License

## 联系我们

如有问题或建议，请通过以下方式联系：

- 项目Issues: [GitHub Issues](https://github.com/yonchain/yonchain-ai/issues)
- 邮箱: support@yonchain.com


