# 插件图标功能说明

## 功能概述

本功能为插件系统添加了图标支持，允许插件在安装时自动提取并保存图标文件，前端可以通过API获取插件图标进行展示。

## 主要组件

### 1. YamlPluginParser (插件解析器增强)
- 在解析plugin.yaml时同时提取图标数据
- 将图标数据存储在PluginDescriptor中
- 支持多种图标格式（SVG、PNG、JPG、GIF等）

### 2. PluginDescriptor (插件描述符扩展)
- 新增iconData字段存储图标的二进制数据
- 在解析阶段就完成图标提取，避免重复处理

### 3. PluginIconService (插件图标服务)
- 负责将图标数据保存到文件系统
- 管理图标文件的存储和访问
- 提供图标文件的生命周期管理

### 4. PluginResponse (插件响应对象)
- 专用的插件信息响应DTO，替代Map结构
- 提供类型安全的数据传输
- 自动包含图标URL字段

### 5. PluginController (插件控制器扩展)
- 添加了图标获取的API接口
- 使用PluginResponse对象统一响应格式
- 支持直接访问图标文件

### 6. PluginStaticResourceConfig (静态资源配置)
- 配置图标文件的Web访问路径
- 支持缓存优化

## API接口

### 获取插件图标
```
GET /plugins/{pluginId}/icon
```
直接返回图标文件，支持浏览器直接显示。

### 获取插件图标URL
```
GET /plugins/{pluginId}/icon-url
```
返回图标的访问URL路径。

### 插件信息API增强
所有返回插件信息的API（如`/plugins`、`/plugins/{id}`、`/plugins/enabled`）现在都使用`PluginResponse`对象，并自动包含`iconUrl`字段。

响应格式示例：
```json
{
  "success": true,
  "message": "Success",
  "data": [
    {
      "id": 1,
      "pluginId": "yonchain.deepseek",
      "name": "deepseek",
      "version": "0.0.2",
      "description": "深度求索提供的模型",
      "author": "yonchain",
      "vendor": null,
      "type": "MODEL",
      "status": "ENABLED",
      "enabled": true,
      "installTime": "2024-12-20T10:30:00",
      "updateTime": "2024-12-20T10:30:00",
      "available": true,
      "iconUrl": "/plugins/yonchain.deepseek/icon"
    }
  ],
  "timestamp": 1703058600000
}
```

## 配置说明

在`application.yml`或`application.properties`中配置图标存储路径：

```yaml
yonchain:
  plugin:
    icon:
      storage-path: /path/to/plugin/icons  # 自定义图标存储路径
```

如果不配置，默认使用系统临时目录下的`yonchain-plugins/icons`目录。

## 插件开发指南

### plugin.yaml配置
在插件的`plugin.yaml`文件中指定图标：

```yaml
id: yonchain.deepseek
name: deepseek
icon: icon_s_en.svg  # 图标文件名
# ... 其他配置
```

### 图标文件位置
将图标文件放在JAR包的`_assets/`目录下：

```
your-plugin.jar
├── plugin.yaml
├── _assets/
│   └── icon_s_en.svg
└── ... 其他文件
```

## 支持的图标格式
- SVG (推荐)
- PNG
- JPG/JPEG
- GIF

## 使用示例

### 前端获取插件列表并显示图标
```javascript
// 获取插件列表
fetch('/plugins')
  .then(response => response.json())
  .then(data => {
    if (data.success) {
      data.data.forEach(plugin => {
        if (plugin.iconUrl) {
          // 显示插件图标
          const img = document.createElement('img');
          img.src = plugin.iconUrl;
          img.alt = plugin.name;
          // 添加到页面
        }
      });
    }
  });
```

### 直接访问图标
```html
<img src="/plugins/yonchain.deepseek/icon" alt="DeepSeek Plugin" />
```

## 架构优势

1. **性能优化**：图标在解析阶段就提取完成，避免在安装时重复读取JAR文件
2. **内存管理**：图标数据存储在PluginDescriptor中，可以根据需要释放
3. **一致性保证**：解析和图标提取在同一个事务中完成，保证数据一致性
4. **扩展性**：图标处理逻辑集中在解析器中，便于扩展和维护

## 注意事项

1. 图标文件会在插件解析时自动提取到内存中
2. 在插件安装时将图标数据保存为`{pluginId}_icon.{ext}`格式的文件
3. 插件卸载时会自动删除对应的图标文件
4. 建议图标文件大小不超过200KB
5. SVG格式图标具有最佳的缩放性和兼容性
6. 图标文件名必须与`plugin.yaml`中的`icon`字段一致
