# 插件重启后自动重新加载解决方案

## 问题描述

在 `ModelPluginAdapter.java` 中，当启动模型插件时会：
1. 注册模型提供商到Spring容器
2. 注册模型提供商到模型工厂  
3. 注册模型到模型注册中心

但是重启后这些数据都会丢失，需要重启时重新加载这些数据。

## 解决方案

### 1. 插件状态持久化

#### 问题原因
- 插件的启用/禁用状态只存在于内存中
- 重启后无法知道哪些插件之前是启用状态

#### 解决方案
**在 `ModelPluginAdapter.java` 中增加状态持久化：**

```java
// 在 onPluginEnable 方法最后添加
updatePluginStatus(pluginId, "enabled");

// 在 onPluginDisable 方法最后添加  
updatePluginStatus(pluginId, "disabled");

// 新增状态更新方法
private void updatePluginStatus(String pluginId, String status) {
    // 更新数据库中的插件状态
}
```

### 2. 启动时自动重新加载

#### 核心组件

**PluginSystemInitializer（新增）**
- 实现 `ApplicationRunner` 接口
- 在应用启动时自动执行
- 查询数据库中状态为 "enabled" 的插件
- 逐个调用插件适配器的 `onPluginEnable` 方法

```java
@Component
@Order(100) // 在模型初始化器之后执行
public class PluginSystemInitializer implements ApplicationRunner {
    @Override
    public void run(ApplicationArguments args) throws Exception {
        pluginManager.loadInstalledPlugins();
    }
}
```

**PluginManager.loadInstalledPlugins()（已存在但未调用）**
- 查询数据库中状态为 "enabled" 的所有插件
- 根据插件类型找到对应的适配器
- 调用适配器的 `onPluginEnable` 方法重新注册

### 3. 配置化支持

#### 应用配置
在 `application.yaml` 中添加：

```yaml
yonchain:
  plugin:
    auto-load-enabled: true      # 是否启用插件自动加载
    auto-load-delay: 5000        # 自动加载延迟时间（毫秒）
    work-dir: ${java.io.tmpdir}/yonchain-plugins  # 插件工作目录
```

#### 自动配置类
```java
@Configuration
public class PluginAutoConfiguration {
    @Bean
    @ConditionalOnProperty(name = "yonchain.plugin.auto-load-enabled", 
                          havingValue = "true", matchIfMissing = true)
    public PluginSystemInitializer pluginSystemInitializer() {
        return new PluginSystemInitializer();
    }
}
```

## 实现流程

### 启动流程
1. Spring Boot 应用启动
2. 所有Bean初始化完成
3. `PluginSystemInitializer.run()` 执行
4. 延迟指定时间（确保Spring容器完全启动）
5. 调用 `PluginManager.loadInstalledPlugins()`
6. 查询数据库获取状态为 "enabled" 的插件列表
7. 遍历插件列表，为每个插件：
   - 根据插件类型获取对应的适配器
   - 调用适配器的 `onPluginEnable()` 方法
   - 重新注册模型提供商和模型到相应的注册中心

### 状态同步流程
1. 用户启用插件 → `ModelPluginAdapter.onPluginEnable()` → 更新数据库状态为 "enabled"
2. 用户禁用插件 → `ModelPluginAdapter.onPluginDisable()` → 更新数据库状态为 "disabled"
3. 应用重启 → 只重新加载状态为 "enabled" 的插件

## 关键优势

1. **数据持久化**：插件状态保存在数据库中，重启不丢失
2. **自动恢复**：重启后自动重新注册所有已启用的插件
3. **可配置**：支持通过配置文件控制自动加载行为
4. **容错性**：单个插件加载失败不影响其他插件和应用启动
5. **时序控制**：通过 `@Order` 和延迟加载确保正确的初始化顺序

## 使用说明

### 启用自动加载（默认启用）
```yaml
yonchain:
  plugin:
    auto-load-enabled: true
    auto-load-delay: 5000
```

### 禁用自动加载
```yaml
yonchain:
  plugin:
    auto-load-enabled: false
```

### 自定义延迟时间
```yaml
yonchain:
  plugin:
    auto-load-delay: 10000  # 延迟10秒
```

重启应用后，所有之前启用的插件都会自动重新加载并注册到相应的服务中。
