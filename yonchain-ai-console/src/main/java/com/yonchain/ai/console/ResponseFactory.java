package com.yonchain.ai.console;

import com.yonchain.ai.api.agent.Agent;
import com.yonchain.ai.api.agent.AgentPublishRecord;
import com.yonchain.ai.api.common.Page;
import com.yonchain.ai.api.exception.YonchainException;
import com.yonchain.ai.api.model.ModelInfo;
import com.yonchain.ai.api.model.ModelProviderInfo;
import com.yonchain.ai.api.sys.*;
import com.yonchain.ai.api.tag.Tag;
import com.yonchain.ai.console.agent.response.AgentPublishRecordResponse;
import com.yonchain.ai.console.agent.response.AppResponse;
import com.yonchain.ai.console.file.entity.FileEntity;
import com.yonchain.ai.console.file.response.FileResponse;
import com.yonchain.ai.console.model.response.ModelConfigResponse;
import com.yonchain.ai.console.model.response.ModelProviderResponse;
import com.yonchain.ai.console.model.response.ModelResponse;
import com.yonchain.ai.console.sys.response.*;
import com.yonchain.ai.console.tag.response.TagResponse;
import com.yonchain.ai.web.response.ListResponse;
import com.yonchain.ai.web.response.PageResponse;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.util.StringUtils;

import java.time.Duration;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 响应工厂类，用于创建和构建API响应对象
 * 提供标准化的响应格式和错误处理机制
 *
 * @author Cgy
 * @since 1.0.0
 */
public class ResponseFactory {

    private final ObjectMapper objectMapper;

    public ResponseFactory() {
        this(new ObjectMapper());
    }

    public ResponseFactory(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }


    /**
     * 创建模型响应对象
     * <p>
     * 将Model实体对象转换为标准化的API响应格式
     * 包含模型的基本信息和状态
     * </p>
     *
     * @param model 模型实体对象，包含模型基本信息，不能为null
     * @return 标准化后的模型响应对象，包含模型ID、名称、描述、状态等信息
     * @see ModelInfo
     * @see ModelResponse
     */
    public ModelResponse createModelResponse(ModelInfo model) {
        ModelResponse response = new ModelResponse();
        // 设置模型ID
        response.setId(model.getId());
        // 设置模型编码
        response.setModelCode(model.getCode());
        // 设置模型名称
        response.setModelName(model.getName());
        // 设置模型类型
        response.setModelType(model.getType());
        // 设置提供商名称
        response.setProvider(model.getProvider());
        // 设置加密配置
        // response.setEncryptedConfig(model.getEncryptedConfig());
        // 设置创建时间
        // response.setCreatedAt(model.getCreatedAt());
        // 设置是否有效
         response.setEnabled(model.getEnabled());
        return response;
    }


    /**
     * 创建模型响应列表
     * <p>
     * 将模型分页数据转换为标准化的API响应列表格式
     * 包含分页元数据和转换后的模型数据列表
     * </p>
     *
     * @param models 模型分页数据对象，包含分页信息和模型数据列表，不能为null
     * @return 标准化后的模型响应列表对象，包含分页元数据和转换后的模型列表
     * @see ModelInfo
     * @see ModelResponse
     * @see PageResponse
     */
    public ListResponse<ModelResponse> createModelListResponse(List<ModelInfo> models) {
        ListResponse<ModelResponse> response = new ListResponse<>();
        response.setData(models.stream()
                .map(this::createModelResponse)
                .toList());
        return response;

    }

    /**
     * 创建模型分页响应对象
     * <p>
     * 将模型分页数据转换为标准化的API分页响应格式
     * 包含分页元数据和转换后的模型数据列表
     * </p>
     *
     * @param models 模型分页数据对象，包含分页信息和模型数据列表，不能为null
     * @return 标准化后的模型分页响应对象，包含分页元数据和转换后的模型列表
     * @see ModelInfo
     * @see ModelResponse
     * @see PageResponse
     */
    public PageResponse<ModelResponse> createModelPageResponse(Page<ModelInfo> models) {
        PageResponse<ModelResponse> response = new PageResponse<>();
        // 设置当前页码
        response.setPageNum(models.getCurrent());
        // 设置每页记录数
        response.setPageSize(models.getSize());
        // 设置总记录数
        response.setTotal(models.getTotal());
        // 转换应用数据列表
        response.setData(models.getRecords().stream()
                .map(this::createModelResponse)
                .filter(Objects::nonNull)
                .toList());
        return response;
    }

    /**
     * 创建模型提供商响应对象
     * <p>
     * 将ModelProvider实体对象转换为标准化的API响应格式
     * 包含模型提供商的基本信息、配置和能力
     * 与YAML提供商配置文件保持一致
     * </p>
     *
     * @param provider 模型提供商实体对象，包含模型提供商基本信息，不能为null
     * @return 标准化后的模型提供商响应对象，包含提供商ID、名称、描述、能力等信息
     * @see ModelProviderInfo
     * @see ModelProviderResponse
     */
    public ModelProviderResponse createModelProviderResponse(ModelProviderInfo provider) {
        ModelProviderResponse response = new ModelProviderResponse();
        // 设置主键ID
        response.setId(provider.getId());
        // 设置提供商唯一标识码
        response.setCode(provider.getCode());
        // 设置提供商显示名称
        response.setName(provider.getName());
        // 设置提供商简要描述信息
        response.setDescription(provider.getDescription());
        // 设置提供商图标
        response.setIcon(provider.getIcon());
        // 设置排序权重
        response.setSortOrder(provider.getSortOrder());
        // 设置该提供商支持的模型类型列表
        response.setSupportedModelTypes(provider.getSupportedModelTypes());
        // 设置提供商配置参数的JSON Schema定义
        response.setConfigSchemas(provider.getConfigSchemas());
        // 设置提供商支持的能力配置
        response.setCapabilities(provider.getCapabilities());
        //是否开启
        response.setEnabled(provider.getEnabled());
        //模型总数
        response.setModelCount(provider.getModelCount());
        return response;
    }

    /**
     * 创建模型提供商分页响应对象
     * <p>
     * 将模型提供商分页数据转换为标准化的API分页响应格式
     * 包含分页元数据和转换后的模型提供商数据列表
     * </p>
     *
     * @param providers 模型提供商分页数据对象，包含分页信息和模型提供商数据列表，不能为null
     * @return 标准化后的模型提供商分页响应对象，包含分页元数据和转换后的模型提供商列表
     * @see ModelProviderInfo
     * @see ModelProviderResponse
     * @see PageResponse
     */
    public PageResponse<ModelProviderResponse> createModelProviderPageResponse(Page<ModelProviderInfo> providers) {
        PageResponse<ModelProviderResponse> response = new PageResponse<>();
        // 设置当前页码
        response.setPageNum(providers.getCurrent());
        // 设置每页记录数
        response.setPageSize(providers.getSize());
        // 设置总记录数
        response.setTotal(providers.getTotal());
        // 转换模型提供商数据列表
        response.setData(providers.getRecords().stream()
                .map(this::createModelProviderResponse)
                .filter(Objects::nonNull)
                .toList());
        return response;
    }

    /**
     * 创建模型提供商列表响应对象
     * <p>
     * 将模型提供商列表转换为标准化的API列表响应格式
     * 包含模型提供商数据列表但不包含分页信息
     * </p>
     *
     * @param providers 模型提供商列表，包含模型提供商数据，不能为null
     * @return 标准化后的模型提供商列表响应对象，包含模型提供商数据列表
     * @see ModelProviderInfo
     * @see ModelProviderResponse
     * @see ListResponse
     */
    public ListResponse<ModelProviderResponse> createModelProviderListResponse(List<ModelProviderInfo> providers) {
        ListResponse<ModelProviderResponse> response = new ListResponse<>();
        response.setData(providers.stream()
                .map(this::createModelProviderResponse)
                .filter(Objects::nonNull)
                .toList());
        return response;
    }


    /**
     * 创建用户响应对象
     * <p>
     * 将User实体对象转换为标准化的API响应格式
     * 包含用户的基本信息和状态
     * </p>
     *
     * @param user 用户实体对象，包含用户基本信息，不能为null
     * @return 标准化后的用户响应对象，包含用户ID、名称、邮箱等信息
     * @see User
     * @see UserResponse
     */
    public UserResponse createUserResponse(User user) {
        UserResponse response = new UserResponse();
        // 设置用户ID
        response.setId(user.getId());
        // 设置用户名称
        response.setName(user.getName());
        // 设置用户邮箱
        response.setEmail(user.getEmail());
        // 设置用户头像URL
        response.setAvatar(user.getAvatar());
        // 设置用户状态
        response.setStatus(user.getStatus());
        // 设置创建时间
        response.setCreatedAt(user.getCreatedAt());
        //设置最后活跃时间
        response.setLastActiveAt(user.getLastActiveAt());
        return response;
    }

    /**
     * 创建用户分页响应对象
     * 根据传入的用户分页数据构建标准化的分页响应
     *
     * @param users 用户分页数据对象，包含分页信息和用户数据列表
     * @return 标准化后的用户分页响应对象
     */
    public PageResponse<UserResponse> createUserPageResponse(Page<User> users) {
        PageResponse<UserResponse> response = new PageResponse<>();
        // 设置当前页码
        response.setPageNum(users.getCurrent());
        // 设置每页记录数
        response.setPageSize(users.getSize());
        // 设置总记录数
        response.setTotal(users.getTotal());
        // 设置用户数据列表
        response.setData(users.getRecords().stream()
                .map(this::createUserResponse)
                .toList());
        return response;
    }


    /**
     * 创建角色响应对象
     * <p>
     * 将Role实体对象转换为标准化的API响应格式
     * 包含角色的基本信息和权限配置
     * </p>
     *
     * @param role 角色实体对象，包含角色基本信息，不能为null
     * @return 标准化后的角色响应对象，包含角色ID、名称、编码等信息
     * @see Role
     * @see RoleResponse
     */
    public RoleResponse createRoleResponse(Role role) {
        RoleResponse roleResponse = new RoleResponse();
        // 设置角色ID
        roleResponse.setId(role.getId());
        // 设置角色名称
        roleResponse.setName(role.getName());
        // 设置角色编码
        roleResponse.setCode(role.getCode());
        // 设置角色描述
        roleResponse.setDescription(role.getDescription());
        // 设置角色状态
        roleResponse.setStatus(role.getStatus());
        // 设置创建时间
        roleResponse.setCreatedAt(role.getCreatedAt());
        // 设置创建人
        roleResponse.setCreatedBy(role.getCreatedBy());
        // 设置更新时间
        roleResponse.setUpdatedAt(role.getUpdatedAt());
        // 设置更新人
        roleResponse.setUpdatedBy(role.getUpdatedBy());
        //设置角色类别
        roleResponse.setCategory(role.getCategory());
        return roleResponse;
    }

    /**
     * 创建角色分页响应对象
     * <p>
     * 将角色分页数据转换为标准化的API分页响应格式
     * 包含分页元数据和转换后的角色数据列表
     * </p>
     *
     * @param page 角色分页数据对象，包含分页信息和角色数据列表，不能为null
     * @return 标准化后的角色分页响应对象，包含分页元数据和角色列表
     * @see Role
     * @see RoleResponse
     * @see PageResponse
     */
    public PageResponse<RoleResponse> createRolePageResponse(Page<Role> page) {
        PageResponse<RoleResponse> response = new PageResponse<>();
        // 设置当前页码
        response.setPageNum(page.getCurrent());
        // 设置每页记录数
        response.setPageSize(page.getSize());
        // 设置总记录数
        response.setTotal(page.getTotal());
        // 设置角色数据列表
        response.setData(page.getRecords().stream()
                .map(this::createRoleResponse)
                .toList());
        return response;
    }

    /**
     * 创建角色列表响应对象
     * <p>
     * 将角色列表转换为标准化的API列表响应格式
     * 包含角色数据列表但不包含分页信息
     * </p>
     *
     * @param roles 角色列表，包含角色数据，不能为null
     * @return 标准化后的角色列表响应对象，包含角色数据列表
     * @see Role
     * @see RoleResponse
     * @see ListResponse
     */
    public ListResponse<RoleResponse> createRoleListResponse(List<Role> roles) {
        ListResponse<RoleResponse> response = new ListResponse<>();
        response.setData(roles.stream()
                .map(this::createRoleResponse)
                .toList());
        return response;
    }

    /**
     * 创建租户响应对象
     * <p>
     * 将Tenant实体对象转换为标准化的API响应格式
     * 包含租户的基本信息和配置
     * </p>
     *
     * @param tenant 租户实体对象，包含租户基本信息，不能为null
     * @return 标准化后的租户响应对象，包含租户ID、名称、计划等信息
     * @see Tenant
     * @see TenantResponse
     */
    public TenantResponse createTenantResponse(Tenant tenant) {
        TenantResponse tenantResponse = new TenantResponse();
        // 设置租户ID
        tenantResponse.setId(tenant.getId());
        // 设置租户名称
        tenantResponse.setName(tenant.getName());
        // 设置租户计划
        tenantResponse.setPlan(tenant.getPlan());
        // 设置租户状态
        tenantResponse.setStatus(tenant.getStatus());
        // 设置创建时间
        tenantResponse.setCreatedAt(tenant.getCreatedAt());
        // 设置更新时间
        tenantResponse.setUpdatedAt(tenant.getUpdatedAt());
        // 设置是否当前租户
        tenantResponse.setCurrent(tenant.getCurrent());
        return tenantResponse;
    }

    /**
     * 创建租户分页响应对象
     * <p>
     * 将租户分页数据转换为标准化的API分页响应格式
     * 包含分页元数据和转换后的租户数据列表
     * </p>
     *
     * @param tenantPage 租户分页数据对象，包含分页信息和租户数据列表，不能为null
     * @return 标准化后的租户分页响应对象，包含分页元数据和租户列表
     * @see Tenant
     * @see TenantResponse
     * @see PageResponse
     */
    public PageResponse<TenantResponse> createTenantPageResponse(Page<Tenant> tenantPage) {
        PageResponse<TenantResponse> response = new PageResponse<>();
        // 设置当前页码
        response.setPageNum(tenantPage.getCurrent());
        // 设置每页记录数
        response.setPageSize(tenantPage.getSize());
        // 设置总记录数
        response.setTotal(tenantPage.getTotal());
        // 设置租户数据列表
        response.setData(tenantPage.getRecords().stream()
                .map(this::createTenantResponse)
                .toList());
        return response;
    }

    /**
     * 创建租户列表响应对象
     * <p>
     * 将租户列表转换为标准化的API列表响应格式
     * 包含租户数据列表但不包含分页信息
     * </p>
     *
     * @param tenant 租户列表，包含租户数据，不能为null
     * @return 标准化后的租户列表响应对象，包含租户数据列表
     * @see Tenant
     * @see TenantResponse
     * @see ListResponse
     */
    public ListResponse<TenantResponse> createTenantListResponse(List<Tenant> tenant) {
        ListResponse<TenantResponse> response = new ListResponse<>("workspaces");
        response.setData(tenant.stream()
                .map(this::createTenantResponse)
                .toList());
        return response;
    }


    /**
     * 创建应用响应对象
     * <p>
     * 将App实体对象转换为标准化的API响应格式
     * 包含应用的基本信息、状态和时间戳
     * </p>
     *
     * @param app 应用实体对象，包含应用基本信息，可为null
     * @return 标准化后的应用响应对象，如果输入为null则返回null
     * @throws NumberFormatException 如果状态转换失败
     */
    public AppResponse createAppResponse(Agent app) {
        AppResponse response = new AppResponse();
        // 设置应用ID
        response.setId(app.getId());
        // 设置租户ID
        response.setTenantId(app.getTenantId());
        // 设置应用名称
        response.setName(app.getName());
        // 设置应用图标URL
        response.setIcon(app.getIcon());
        // 设置图标背景色
        response.setIconBackground(app.getIconBackground());
        // 设置应用描述
        response.setDescription(app.getDescription());
        // 设置应用状态
        response.setStatus(app.getStatus());
        // 设置创建时间
        response.setCreatedAt(app.getCreatedAt());
        // 设置更新时间
        response.setUpdatedAt(app.getUpdatedAt());
        return response;
    }

    /**
     * 创建应用分页响应对象
     * <p>
     * 将应用分页数据转换为标准化的API分页响应格式
     * 包含分页元数据和转换后的应用数据列表
     * </p>
     *
     * @param apps 应用分页数据对象，包含分页信息和应用数据列表，不能为null
     * @return 标准化后的应用分页响应对象，包含分页元数据和转换后的应用列表
     * @see Agent
     * @see AppResponse
     * @see PageResponse
     */
    public PageResponse<AppResponse> createAppPageResponse(Page<Agent> apps) {
        PageResponse<AppResponse> response = new PageResponse<>();
        // 设置当前页码
        response.setPageNum(apps.getCurrent());
        // 设置每页记录数
        response.setPageSize(apps.getSize());
        // 设置总记录数
        response.setTotal(apps.getTotal());
        // 转换应用数据列表
        response.setData(apps.getRecords().stream()
                .map(this::createAppResponse)
                .filter(Objects::nonNull)
                .toList());
        return response;
    }

    /**
     * 创建OAuth2注册客户端响应对象
     * <p>
     * 将OAuth2RegisteredClient实体对象转换为标准化的API响应格式
     * 包含OAuth2注册客户端的基本信息和配置
     * </p>
     *
     * @param client OAuth2注册客户端实体对象，包含客户端基本信息，不能为null
     * @return 标准化后的OAuth2注册客户端响应对象，包含客户端ID、名称、密钥等信息
     * @see OAuth2RegisteredClient
     * @see OAuth2RegisteredClientResponse
     */
    public OAuth2RegisteredClientResponse createOAuth2RegisteredClientResponse(OAuth2RegisteredClient client) {
        OAuth2RegisteredClientResponse response = new OAuth2RegisteredClientResponse();
        // 设置客户端ID
        response.setId(client.getId());
        // 设置客户端标识
        response.setClientId(client.getClientId());
        // 设置客户端标识发布时间
        response.setClientIdIssuedAt(client.getClientIdIssuedAt());
        // 设置客户端密钥
        response.setClientSecret(client.getClientSecret().substring("{noop}".length()));
        // 设置客户端名称
        response.setClientName(client.getClientName());
        // 设置授权类型
        response.setAuthorizationGrantTypes(client.getAuthorizationGrantTypes());
        // 设置重定向URI
        response.setRedirectUris(client.getRedirectUris());
        // 设置作用域
        response.setScopes(client.getScopes());

        String tokenSettings = client.getTokenSettings();
        if (StringUtils.hasText(tokenSettings)) {
            try {
                Map<String, Object> tokenMap = objectMapper.readValue(tokenSettings, new TypeReference<>() {
                });
                // 设置访问令牌生存时间（秒）
                if (tokenMap.containsKey("settings.token.access-token-time-to-live")) {
                    Object accessTokenTimeToLiveObj = tokenMap.get("settings.token.access-token-time-to-live");
                    if (accessTokenTimeToLiveObj instanceof List) {
                        // 处理Duration格式 ["java.time.Duration", 7200]
                        List<?> durationList = (List<?>) accessTokenTimeToLiveObj;
                        if (durationList.size() >= 2 && durationList.get(1) instanceof Number) {
                            response.setAccessTokenTimeToLive(((Number) durationList.get(1)).longValue());
                        }
                    } else if (accessTokenTimeToLiveObj instanceof String) {
                        // 直接是数字
                        response.setAccessTokenTimeToLive(Duration.parse((String) accessTokenTimeToLiveObj).toSeconds());
                    }
                }

                // 设置刷新令牌生存时间（秒）
                if (tokenMap.containsKey("settings.token.refresh-token-time-to-live")) {
                    Object refreshTokenTimeToLiveObj = tokenMap.get("settings.token.refresh-token-time-to-live");
                    if (refreshTokenTimeToLiveObj instanceof List) {
                        // 处理Duration格式 ["java.time.Duration", 2592000]
                        List<?> durationList = (List<?>) refreshTokenTimeToLiveObj;
                        if (durationList.size() >= 2 && durationList.get(1) instanceof Number) {
                            response.setRefreshTokenTimeToLive(((Number) durationList.get(1)).longValue());
                        }
                    } else if (refreshTokenTimeToLiveObj instanceof String) {
                        // 直接是数字
                        response.setRefreshTokenTimeToLive(Duration.parse((String) refreshTokenTimeToLiveObj).toSeconds());
                    }
                }
            } catch (JsonProcessingException e) {
                throw new YonchainException("无效的token设置: " + e.getMessage());
            }
        }
        return response;
    }

    /**
     * 创建OAuth2注册客户端分页响应对象
     * <p>
     * 将OAuth2注册客户端分页数据转换为标准化的API分页响应格式
     * 包含分页元数据和转换后的OAuth2注册客户端数据列表
     * </p>
     *
     * @param page OAuth2注册客户端分页数据对象，包含分页信息和客户端数据列表，不能为null
     * @return 标准化后的OAuth2注册客户端分页响应对象，包含分页元数据和客户端列表
     * @see OAuth2RegisteredClient
     * @see OAuth2RegisteredClientResponse
     * @see PageResponse
     */
    public PageResponse<OAuth2RegisteredClientResponse> createOAuth2RegisteredClientPageResponse(Page<OAuth2RegisteredClient> page) {
        PageResponse<OAuth2RegisteredClientResponse> response = new PageResponse<>();
        // 设置当前页码
        response.setPageNum(page.getCurrent());
        // 设置每页记录数
        response.setPageSize(page.getSize());
        // 设置总记录数
        response.setTotal(page.getTotal());
        // 设置OAuth2注册客户端数据列表
        response.setData(page.getRecords().stream()
                .map(this::createOAuth2RegisteredClientResponse)
                .toList());
        return response;
    }


    /**
     * 创建文件响应对象
     * <p>
     * 将FileEntity实体对象转换为标准化的API响应格式
     * 包含文件的基本信息、URL和元数据
     * </p>
     *
     * @param file 文件实体对象，包含文件基本信息，不能为null
     * @return 标准化后的文件响应对象，包含文件ID、名称、URL、大小等信息
     * @see FileEntity
     * @see FileResponse
     */
    public FileResponse createFileResponse(FileEntity file) {
        if (file == null) {
            return null;
        }

        FileResponse response = new FileResponse();
        // 设置文件ID
        response.setId(file.getId());
        // 设置租户ID
        response.setTenantId(file.getTenantId());
        // 设置文件存储类型
        response.setStorageType(file.getStorageType());
        // 设置文件存储键
        response.setKey(file.getKey());
        // 设置文件名称
        response.setName(file.getName());
        // 设置文件大小(字节)
        response.setSize(file.getSize());
        // 设置文件扩展名
        response.setExtension(file.getExtension());
        // 设置文件MIME类型
        response.setMimeType(file.getMimeType());
        // 设置创建人ID
        response.setCreatedBy(file.getCreatedBy());
        // 设置创建时间
        response.setCreatedAt(file.getCreatedAt());
        // 设置是否已使用
        response.setUsed(file.isUsed());
        // 设置使用者ID
        response.setUsedBy(file.getUsedBy());
        // 设置使用时间
        response.setUsedAt(file.getUsedAt());
        // 设置文件哈希值
        response.setHash(file.getHash());
        // 设置创建人角色
        response.setCreatedByRole(file.getCreatedByRole());
        // 设置源文件URL
        response.setSourceUrl(file.getSourceUrl());
        return response;
    }

    /**
     * 创建文件分页响应对象
     * <p>
     * 将文件分页数据转换为标准化的API分页响应格式
     * 包含分页元数据和转换后的文件数据列表
     * </p>
     *
     * @param files 文件分页数据对象，包含分页信息和文件数据列表，不能为null
     * @return 标准化后的文件分页响应对象，包含分页元数据和文件列表
     * @see FileEntity
     * @see FileResponse
     * @see PageResponse
     */
    public PageResponse<FileResponse> createFilePageResponse(Page<FileEntity> files) {
        PageResponse<FileResponse> response = new PageResponse<>();
        // 设置当前页码
        response.setPageNum(files.getCurrent());
        // 设置每页记录数
        response.setPageSize(files.getSize());
        // 设置总记录数
        response.setTotal(files.getTotal());
        // 设置文件数据列表
        response.setData(files.getRecords().stream()
                .map(this::createFileResponse)
                .filter(Objects::nonNull)
                .toList());
        return response;
    }

    /**
     * 创建标签响应对象
     * <p>
     * 将Tag实体对象转换为标准化的API响应格式
     * 包含标签的基本信息和关联租户ID
     * </p>
     *
     * @param tag 标签实体对象，包含标签基本信息，不能为null
     * @return 标准化后的标签响应对象，包含标签ID、名称、创建时间和租户ID
     * @see Tag
     * @see TagResponse
     */
    public TagResponse createTagResponse(Tag tag) {
        TagResponse response = new TagResponse();
        // 设置标签ID
        response.setId(tag.getId());
        // 设置标签名称
        response.setName(tag.getName());
        // 设置创建时间
        response.setCreatedAt(tag.getCreatedAt());
        // 设置租户ID
        response.setTenantId(tag.getTenantId());
        return response;
    }

    /**
     * 创建标签分页响应对象
     * <p>
     * 将标签分页数据转换为标准化的API分页响应格式
     * 包含分页元数据和转换后的标签数据列表
     * </p>
     *
     * @param tags 标签分页数据对象，包含分页信息和标签数据列表，不能为null
     * @return 标准化后的标签分页响应对象，包含分页元数据和标签列表
     * @see Tag
     * @see TagResponse
     * @see PageResponse
     */
    public PageResponse<TagResponse> createTagPageResponse(Page<Tag> tags) {
        PageResponse<TagResponse> response = new PageResponse<>();
        // 设置当前页码
        response.setPageNum(tags.getCurrent());
        // 设置每页记录数
        response.setPageSize(tags.getSize());
        // 设置总记录数
        response.setTotal(tags.getTotal());
        // 设置标签数据列表
        response.setData(tags.getRecords().stream()
                .map(this::createTagResponse)
                .toList());
        return response;
    }

    /**
     * 创建标签响应对象列表
     * <p>
     * 将标签列表转换为标准化的API响应格式
     * 包含标签基本信息
     * </p>
     *
     * @param tags 标签列表
     * @return 标准化后的标签响应对象列表，包含标签ID、名称、创建时间和租户ID
     * @see Tag
     * @see TagResponse
     */
    public ListResponse<TagResponse> createTagResponseList(List<Tag> tags) {
        ListResponse<TagResponse> response = new ListResponse<>();
        response.setData(tags.stream().map(this::createTagResponse).toList());
        return response;
    }

    /**
     * 创建模型配置响应对象
     * <p>
     * 将ModelInfo实体对象转换为标准化的模型配置API响应格式
     * 包含模型的基本信息和配置项列表
     * </p>
     *
     * @param model 模型实体对象，包含模型配置信息，不能为null
     * @return 标准化后的模型配置响应对象，包含模型代码、名称、启用状态和配置项
     * @throws IllegalArgumentException 如果model参数为null
     * @see ModelInfo
     * @see ModelConfigResponse
     */
    public ModelConfigResponse createModelConfigResponse(ModelInfo model) {
        if (model == null) {
            throw new IllegalArgumentException("Model cannot be null");
        }

        ModelConfigResponse response = new ModelConfigResponse();
        
        // 设置模型代码（使用模型名称作为代码）
        response.setModelCode(model.getName());
        
        // 设置模型名称
        response.setModelName(model.getName());
        
        // 设置是否启用（根据状态判断，假设状态为1表示启用）
        response.setEnabled(model.getEnabled());
        
        // 处理配置项列表
        if (model.getConfigSchemas() != null && !model.getConfigSchemas().isEmpty()) {
            List<ModelConfigResponse.ConfigItem> configItems = model.getConfigSchemas().stream()
                .map(entry -> {
                    ModelConfigResponse.ConfigItem item = new ModelConfigResponse.ConfigItem();
                    item.setKey(entry.getName());
                    item.setValue(entry.getValue());
                    // 根据值类型设置type
                    if (entry.getValue() instanceof Number) {
                        item.setType("number");
                    } else if (entry.getValue() instanceof Boolean) {
                        item.setType("boolean");
                    } else {
                        item.setType("string");
                    }
                    item.setTitle(entry.getTitle());
                    item.setRequired(false);
                    return item;
                })
                .collect(Collectors.toList());
            response.setConfigItems(configItems);
        }
        
        return response;
    }


    /**
     * 创建Agent发布记录分页响应对象
     * <p>
     * 将Agent发布记录分页数据转换为标准化的API分页响应格式
     * 包含分页元数据和转换后的Agent发布记录数据列表
     * </p>
     *
     * @param records Agent发布记录分页数据对象，包含分页信息和发布记录数据列表，不能为null
     * @return 标准化后的Agent发布记录分页响应对象，包含分页元数据和发布记录列表
     * @see AgentPublishRecord
     * @see AgentPublishRecordResponse
     * @see PageResponse
     */
    public PageResponse<AgentPublishRecordResponse> createAgentPublishRecordPageResponse(Page<AgentPublishRecord> records) {
        PageResponse<AgentPublishRecordResponse> pageResponse = new PageResponse<>();
        // 设置当前页码
        pageResponse.setPageNum(records.getCurrent());
        // 设置每页记录数
        pageResponse.setPageSize(records.getSize());
        // 设置总记录数
        pageResponse.setTotal(records.getTotal());
        // 转换发布记录数据列表
        pageResponse.setData(records.getRecords().stream()
                .map(this::createAgentPublishRecordResponse)
                .filter(Objects::nonNull)
                .toList());
        return pageResponse;
    }

    /**
     * 创建Agent发布记录响应对象
     * <p>
     * 将AgentPublishRecord实体对象转换为标准化的API响应格式
     * 包含Agent发布记录的基本信息、状态和时间戳
     * </p>
     *
     * @param record Agent发布记录实体对象，包含发布记录基本信息，不能为null
     * @return 标准化后的Agent发布记录响应对象，包含记录ID、版本、状态等信息
     * @see AgentPublishRecord
     * @see AgentPublishRecordResponse
     */
    public AgentPublishRecordResponse createAgentPublishRecordResponse(AgentPublishRecord record) {
        AgentPublishRecordResponse response = new AgentPublishRecordResponse();
        // 设置记录ID
        response.setId(record.getId());
        // 设置Agent ID
        response.setAgentId(record.getAgentId());
        // 设置版本号
        response.setVersion(record.getVersion());
        // 设置发布状态
        response.setStatus(record.getStatus());
        // 设置发布描述
        response.setDescription(record.getDescription());
        // 设置发布者ID
        response.setPublishedBy(record.getPublishedBy());
        // 设置发布时间
        response.setPublishedAt(record.getPublishedAt());

        return response;
    }
}
