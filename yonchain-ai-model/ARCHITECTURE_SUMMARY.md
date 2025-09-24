# 永链AI完全替换架构总结

## 🎯 核心设计原则

1. **MyBatis风格架构** - 采用熟悉的Builder模式和配置文件管理
2. **完全替换方案** - 移除所有旧的服务层，直接使用ModelClient
3. **配置驱动** - 通过XML配置文件管理所有模型
4. **命名空间隔离** - 支持提供商和业务场景的命名空间
5. **OpenAI兼容** - 保持OpenAI API完全兼容

## 📁 新架构文件结构

```
yonchain-ai-model/src/main/java/com/yonchain/ai/
├── filter/
│   ├── BaseModelFilter.java                  # 基础过滤器抽象类
│   ├── ChatModelFilter.java                  # 聊天模型专用过滤器
│   ├── ImageModelFilter.java                 # 图像模型专用过滤器
│   ├── EmbeddingModelFilter.java             # 嵌入模型专用过滤器
│   └── AudioModelFilter.java                 # 音频模型专用过滤器
├── config/
│   └── FilterConfiguration.java              # 过滤器注册配置
├── model/
│   ├── core/                                 # 核心架构
│   │   ├── ModelClient.java                  # 模型客户端接口
│   │   ├── ModelClientFactory.java           # 客户端工厂接口
│   │   ├── ModelClientFactoryBuilder.java    # 工厂构建器
│   │   ├── ModelConfiguration.java           # 配置管理中心
│   │   └── impl/
│   │       ├── DefaultModelClient.java       # 默认客户端实现
│   │       └── DefaultModelClientFactory.java # 默认工厂实现
│   ├── factory/                              # 工厂层
│   │   ├── ChatModelFactory.java             # 聊天模型工厂接口
│   │   ├── ImageModelFactory.java            # 图像模型工厂接口
│   │   ├── EmbeddingModelFactory.java        # 嵌入模型工厂接口
│   │   ├── NamespaceModelFactory.java        # 命名空间工厂接口
│   │   └── impl/
│   │       ├── OpenAINamespaceFactory.java   # OpenAI命名空间工厂
│   │       └── DeepSeekNamespaceFactory.java # DeepSeek命名空间工厂
│   ├── registry/                             # 注册中心
│   │   ├── ModelRegistry.java                # 模型注册中心
│   │   ├── ModelFactoryRegistry.java     # 命名空间工厂注册中心
│   │   └── TypeHandlerRegistry.java          # 类型处理器注册中心
│   ├── request/                              # 请求类
│   │   ├── ChatRequest.java                  # 聊天请求
│   │   ├── ImageRequest.java                 # 图像请求
│   │   └── EmbeddingRequest.java             # 嵌入请求
│   ├── definition/                           # 定义类
│   │   └── ModelDefinition.java              # 模型定义
│   ├── typehandler/                          # 类型处理器
│   │   └── TypeHandler.java                  # 类型处理器接口
│   ├── config/                               # 配置解析器
│   │   ├── XMLConfigBuilder.java             # XML配置解析器
│   │   ├── YAMLConfigBuilder.java            # YAML配置解析器
│   │   └── ModelClientAutoConfiguration.java # 自动配置类
│   └── util/                                 # 工具类
│       ├── ModelIdParser.java                # 模型ID解析工具
│       └── Resources.java                    # 资源加载工具
└── example/
    ├── ModelClientExample.java               # 原始示例
    └── NewArchitectureExample.java           # 新架构示例

yonchain-ai-model/src/main/resources/
├── model-config.xml                          # 主配置文件
└── models/
    ├── openai.xml                            # OpenAI模型配置
    ├── deepseek.xml                          # DeepSeek模型配置
    └── business.xml                          # 业务场景模型配置
```

## 🔄 请求处理流程

```
用户请求 → API网关 → 专门过滤器 → ModelClient → NamespaceFactory → Spring AI Model → 外部API
```

### 详细流程：

1. **用户发送请求**: `POST /chat/completions {"model": "openai:gpt-4", ...}`
2. **ChatModelFilter拦截**: 专门处理聊天模型请求
3. **解析模型ID**: `openai:gpt-4` → namespace=`openai`, model=`gpt-4`
4. **ModelClient路由**: 根据命名空间找到对应的工厂
5. **创建模型实例**: OpenAINamespaceFactory创建ChatModel
6. **调用Spring AI**: 使用标准Spring AI接口
7. **返回OpenAI格式**: ChatModelFilter转换为OpenAI兼容的响应格式

### 不同模型类型的处理：

| API端点 | 专门过滤器 | 处理逻辑 |
|---------|-----------|----------|
| `/chat/completions` | ChatModelFilter | 处理聊天对话，支持流式响应 |
| `/images/generations` | ImageModelFilter | 处理图像生成请求 |
| `/embeddings` | EmbeddingModelFilter | 处理文本向量化请求 |
| `/audio/transcriptions` | AudioModelFilter | 处理语音转文字请求 |
| `/audio/speech` | AudioModelFilter | 处理文字转语音请求 |

## 🎯 核心优势

### 1. 极简化架构
- ❌ 移除：`ChatModelService`、`ImageModelService`等中间层
- ❌ 移除：原有的`ModelRegistry`、`ModelFactory`复杂逻辑
- ✅ 保留：`ModelFilter`（处理OpenAI格式转换）
- ✅ 新增：`ModelClient`（统一模型调用入口）

### 2. 配置驱动
```xml
<!-- 命名空间模型配置 -->
<models namespace="openai">
    <model id="gpt-4" type="chat">
        <endpoint>https://api.openai.com/v1/chat/completions</endpoint>
        <auth type="bearer">${openai.apiKey}</auth>
        <options>
            <model>gpt-4</model>
            <temperature>0.7</temperature>
        </options>
    </model>
</models>
```

### 3. 业务场景支持
```xml
<!-- 业务场景配置 -->
<models namespace="business">
    <model id="customer-service" type="chat">
        <endpoint>https://api.openai.com/v1/chat/completions</endpoint>
        <auth type="bearer">${openai.apiKey}</auth>
        <options>
            <model>gpt-4</model>
            <temperature>0.3</temperature>
            <systemPrompt>你是一个专业的客服助手...</systemPrompt>
        </options>
    </model>
</models>
```

## 🚀 使用示例

### MyBatis风格的调用方式：
```java
// 1. 构建工厂（类似SqlSessionFactoryBuilder）
ModelClientFactory factory = new ModelClientFactoryBuilder()
    .build("model-config.xml");

// 2. 创建客户端（类似SqlSession）
try (ModelClient client = factory.createClient()) {
    
    // 3. 调用模型（类似Mapper方法调用）
    ChatResponse response = client.chat("openai:gpt-4", 
        ChatRequest.builder()
            .message("Hello, AI!")
            .build());
    
    // 4. 使用别名调用
    ChatResponse response2 = client.chat("default-chat", request);
    
    // 5. 使用业务场景
    ChatResponse response3 = client.chat("business:customer-service", request);
}
```

## 🔧 扩展支持

### 1. 添加新的提供商
```java
@Component
public class CustomNamespaceFactory implements NamespaceModelFactory {
    @Override
    public String namespace() {
        return "custom";
    }
    
    @Override
    public ChatModel createChatModel(ModelDefinition definition, TypeHandlerRegistry registry) {
        return new CustomChatModel(definition);
    }
}
```

### 2. 添加新的模型配置
```xml
<models namespace="custom">
    <model id="custom-model" type="chat">
        <endpoint>https://api.custom.com/v1/chat</endpoint>
        <auth type="bearer">${custom.apiKey}</auth>
        <options>
            <model>custom-model-v1</model>
        </options>
    </model>
</models>
```

## 📊 与原架构对比

| 特性 | 原架构 | 新架构 |
|------|--------|--------|
| 调用链长度 | 7层 | 4层 |
| 配置方式 | application.yml | XML配置文件 |
| 模型管理 | 代码+配置混合 | 纯配置驱动 |
| 业务场景支持 | 无 | 完整支持 |
| 别名支持 | 无 | 完整支持 |
| 扩展性 | 需要写代码 | 仅需配置 |
| MyBatis风格 | 无 | 完整支持 |

## 🎉 总结

新架构通过完全替换的方式，实现了：

1. **极简化调用链** - 从7层减少到4层
2. **配置驱动** - 所有模型通过XML配置管理
3. **业务场景支持** - 支持为不同业务场景配置专用模型
4. **MyBatis风格** - 熟悉的开发模式，易于理解和使用
5. **完全兼容** - 保持OpenAI API完全兼容
6. **高度可扩展** - 新增模型和提供商仅需配置

这个架构既保持了系统的简洁性，又提供了强大的功能和灵活性，是一个真正意义上的架构升级。
