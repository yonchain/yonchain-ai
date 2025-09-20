# 插件预览API使用说明

## 概述

新增了插件预览功能，允许用户在安装插件前上传插件文件并查看插件的基本信息，包括插件元数据和图标。

## API 接口

### 1. 预览插件信息

**接口**: `POST /plugins/preview`

**描述**: 上传插件JAR文件，解析并返回插件基本信息，但不进行实际安装。

**请求参数**:
- `file`: MultipartFile - 插件JAR文件

**响应格式**:
```json
{
  "success": true,
  "message": "Success",
  "data": {
    "pluginId": "yonchain.deepseek",
    "name": "deepseek",
    "version": "0.0.2",
    "description": {
      "en_US": "Models provided by DeepSeek AI, including deepseek-chat, deepseek-reasoner, and deepseek-coder.",
      "zh_Hans": "深度求索提供的模型，包括 deepseek-chat、deepseek-reasoner、deepseek-coder 等。"
    },
    "author": "yonchain",
    "type": "model",
    "createdAt": "2024-09-20T12:13:50",
    "label": {
      "en_US": "DeepSeek",
      "zh_Hans": "深度求索"
    },
    "iconFileName": "icon_s_en.svg",
    "iconUrl": "/plugins/temp-icons/temp_yonchain.deepseek_1726820830123.svg",
    "pluginClass": "com.yonchain.ai.plugin.deepseek.DeepSeekPlugin",
    "hasIcon": true
  },
  "timestamp": 1726820830123
}
```

**错误响应**:
```json
{
  "success": false,
  "message": "Plugin format error: Invalid plugin descriptor",
  "data": null,
  "timestamp": 1726820830123
}
```

### 2. 获取临时图标

**接口**: `GET /plugins/temp-icons/{iconId}`

**描述**: 获取预览时生成的临时图标文件。

**请求参数**:
- `iconId`: String - 临时图标ID（从预览接口的iconUrl中提取）

**响应**: 图标文件内容（支持SVG、PNG、JPG、GIF等格式）

## 使用示例

### 1. 使用curl预览插件

```bash
curl -X POST \
  http://localhost:8080/plugins/preview \
  -H 'Content-Type: multipart/form-data' \
  -F 'file=@/path/to/your/plugin.jar'
```

### 2. 使用JavaScript预览插件

```javascript
const formData = new FormData();
formData.append('file', fileInput.files[0]);

fetch('/plugins/preview', {
    method: 'POST',
    body: formData
})
.then(response => response.json())
.then(data => {
    if (data.success) {
        const plugin = data.data;
        console.log('插件名称:', plugin.name);
        console.log('插件版本:', plugin.version);
        console.log('插件作者:', plugin.author);
        
        // 显示图标
        if (plugin.hasIcon) {
            const img = document.createElement('img');
            img.src = plugin.iconUrl;
            document.body.appendChild(img);
        }
    } else {
        console.error('预览失败:', data.message);
    }
})
.catch(error => {
    console.error('请求失败:', error);
});
```

## 特性说明

1. **文件验证**: 只接受`.jar`格式的文件
2. **临时存储**: 插件文件仅用于解析，解析完成后立即删除
3. **图标处理**: 如果插件包含图标，会生成临时访问URL
4. **多语言支持**: 支持多语言的描述和标签信息
5. **错误处理**: 提供详细的错误信息，区分解析错误和系统错误

## 注意事项

1. 临时图标文件仅在预览时生成，正式安装时会重新处理
2. 预览不会对系统造成任何影响，不会创建插件记录
3. 建议在正式安装前使用预览功能验证插件信息
4. 临时文件会自动清理，不会占用大量存储空间
