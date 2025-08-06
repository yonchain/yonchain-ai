# 自定义OAuth2授权码流程

本模块实现了一个自定义的OAuth2授权码流程，允许在不需要用户认证的情况下生成授权码，然后使用标准的OAuth2 token端点获取访问令牌。

## 流程说明

1. **获取授权码**：
   - 端点：`/oauth/authorize`
   - 方法：GET 或 POST
   - 参数：
     - `client_id`：客户端ID（必需）
     - `redirect_uri`：重定向URI（必需）
     - `scope`：请求的权限范围，多个权限用空格分隔（可选）
     - `state`：客户端状态，会在响应中原样返回（可选）
     - `response_type`：响应类型，固定为"code"（可选，默认为"code"）
   - 响应：自动重定向到重定向地址，并且附带授权码


2. **使用授权码获取访问令牌**：
   - 端点：`/oauth2/token`
   - 方法：POST
   - 参数：
     - `grant_type`：授权类型，固定为"authorization_code"
     - `code`：授权码
     - `redirect_uri`：重定向URI，必须与获取授权码时提供的一致
     - `client_id`：客户端ID
     - `client_secret`：客户端密钥
   - 响应：JSON格式，包含访问令牌、刷新令牌、过期时间等
     ```json
     {
       "access_token": "访问令牌",
       "token_type": "bearer",
       "expires_in": 过期时间（秒）,
       "refresh_token": "刷新令牌",
       "scope": "权限范围"
     }
     ```

## 测试工具

为了方便测试，提供了以下测试端点：

1. **流程说明**：
   - 端点：`/oauth2-test/flow-info`
   - 方法：GET
   - 响应：JSON格式，包含流程说明

2. **验证授权码**：
   - 端点：`/oauth2-test/verify-code`
   - 方法：GET
   - 参数：
     - `code`：授权码
   - 响应：JSON格式，包含授权码信息

3. **验证访问令牌**：
   - 端点：`/oauth2-test/verify-token`
   - 方法：GET
   - 参数：
     - `token`：访问令牌
   - 响应：JSON格式，包含访问令牌信息

## 示例

### 1. 获取授权码

```http
POST /custom/oauth2/authorize
Content-Type: application/x-www-form-urlencoded

client_id=client&redirect_uri=http://localhost:8080/callback&scope=read write&state=xyz
```

响应：

```json
{
  "code": "授权码",
  "state": "xyz"
}
```

### 2. 验证授权码

```http
GET /oauth2-test/verify-code?code=授权码
```

### 3. 获取访问令牌

```http
POST /oauth2/token
Content-Type: application/x-www-form-urlencoded

grant_type=authorization_code&code=授权码&redirect_uri=http://localhost:8080/callback&client_id=client&client_secret=secret
```

### 4. 验证访问令牌

```http
GET /oauth2-test/verify-token?token=访问令牌
```

## 注意事项

1. 授权码有效期为5分钟
2. 客户端ID、重定向URI和作用域必须在系统中注册
3. 获取授权码时不需要用户认证，但获取访问令牌时需要提供客户端密钥
4. 此实现仅用于特定场景，不符合标准OAuth2规范中的用户授权流程
