package com.yonchain.ai.console.idm.controller;

import com.yonchain.ai.api.common.Page;
import com.yonchain.ai.api.exception.Dify4jException;
import com.yonchain.ai.api.exception.Dify4jForbiddenException;
import com.yonchain.ai.api.exception.Dify4jResourceNotFoundException;
import com.yonchain.ai.api.idm.DefaultOAuth2RegisteredClient;
import com.yonchain.ai.api.idm.OAuth2ClientService;
import com.yonchain.ai.api.idm.OAuth2RegisteredClient;
import com.yonchain.ai.console.BaseController;
import com.yonchain.ai.console.idm.request.OAuth2RegisteredClientQueryRequest;
import com.yonchain.ai.console.idm.request.OAuth2RegisteredClientRequest;
import com.yonchain.ai.console.idm.response.OAuth2RegisteredClientResponse;
import com.yonchain.ai.web.response.ApiResponse;
import com.yonchain.ai.web.response.PageResponse;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.module.SimpleModule;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.time.Duration;
import java.util.*;

/**
 * OAuth2注册客户端控制器
 *
 * @author Cgy
 * @since 1.0.0
 */
@Tag(name = "OAuth2注册客户端管理")
@RestController
@RequestMapping("/oauth2_clients")
public class OAuth2ClientController extends BaseController {

    @Autowired
    private OAuth2ClientService oauth2ClientService;

    //@Autowired
    private ObjectMapper objectMapper = createObjectMapper();

    @Operation(summary = "根据ID获取OAuth2注册客户端")
    @GetMapping("/{id}")
    public OAuth2RegisteredClientResponse getOAuth2RegisteredClientById(
            @Parameter(description = "客户端ID") @PathVariable String id) {
        OAuth2RegisteredClient client = oauth2ClientService.getById(id);
        if (client == null) {
            throw new Dify4jResourceNotFoundException("客户端不存在");
        }
        return responseFactory.createOAuth2RegisteredClientResponse(client);
    }

    @Operation(summary = "分页查询OAuth2注册客户端")
    @GetMapping
    public PageResponse<OAuth2RegisteredClientResponse> pageOAuth2RegisteredClients(
            @Valid OAuth2RegisteredClientQueryRequest request) {
        Map<String, Object> params = new HashMap<>();
        params.put("clientId", request.getClientId());
        params.put("clientName", request.getClientName());

        Page<OAuth2RegisteredClient> page = oauth2ClientService.pageOAuth2RegisteredClients(
                params, request.getPage(), request.getLimit());

        return responseFactory.createOAuth2RegisteredClientPageResponse(page);
    }

    @Operation(summary = "创建OAuth2注册客户端")
    @PostMapping
    public ApiResponse<Void> createOAuth2RegisteredClient(
            @Valid @RequestBody OAuth2RegisteredClientRequest request) {

        OAuth2RegisteredClient auth2RegisteredClient = oauth2ClientService.getByClientId(request.getClientId());
        if (auth2RegisteredClient != null) {
            throw new Dify4jException("客户端Id已存在");
        }

        DefaultOAuth2RegisteredClient client = new DefaultOAuth2RegisteredClient();
        client.setId(UUID.randomUUID().toString());
        client.setClientId(request.getClientId());
        client.setClientSecret("{noop}" + request.getClientSecret());
        client.setClientName(request.getClientName());
        client.setClientAuthenticationMethods("client_secret_basic");
        client.setAuthorizationGrantTypes(request.getAuthorizationGrantTypes());
        client.setRedirectUris(request.getRedirectUris());
        client.setScopes(request.getScopes());

        //token设置
        Map<String, Object> settings = new HashMap<>();
        //访问令牌有效期
        settings.put("settings.token.access-token-time-to-live", Duration.ofSeconds(request.getAccessTokenTimeToLive()));
        //如果是刷新模式，则设置默认值
        if (request.getAuthorizationGrantTypes().contains("refresh_token")) {
            //刷新令牌有效期
            settings.put("settings.token.refresh-token-time-to-live", Duration.ofSeconds(request.getRefreshTokenTimeToLive()));
            // 是否重用刷新令牌，默认true
            settings.put("settings.token.reuse-refresh-tokens", false);
        }

        Map<String, Object> finalMap = new HashMap<>();
        finalMap.put("@class", "java.util.Collections$UnmodifiableMap");
        finalMap.putAll(Collections.unmodifiableMap(settings));
        try {
            client.setTokenSettings(objectMapper.writeValueAsString(finalMap));
        } catch (JsonProcessingException e) {
            throw new Dify4jException("无效的token设置: " + e.getMessage());
        }
        client.setClientSettings("{\"@class\":\"java.util.Collections$UnmodifiableMap\",\"settings.client.require-proof-key\":false,\"settings.client.require-authorization-consent\":false}");

        oauth2ClientService.createOAuth2RegisteredClient(client);
        return ApiResponse.success();
    }

    @Operation(summary = "更新OAuth2注册客户端")
    @PutMapping("/{id}")
    public ApiResponse<Void> updateOAuth2RegisteredClient(
            @Parameter(description = "客户端ID") @PathVariable String id,
            @Valid @RequestBody OAuth2RegisteredClientRequest request) {
        OAuth2RegisteredClient client = oauth2ClientService.getById(id);

        //判断是否存在
        if (client == null) {
            throw new Dify4jResourceNotFoundException("客户端不存在");
        }

        //判断是否修改了系统默认的客户端名称
        if ("dify4j".equals(client.getClientId()) && !request.getClientId().equals("dify4j")) {
            throw new Dify4jForbiddenException("系统默认客户端名称不允许修改");
        }

        //客户端Id是否已存在
        OAuth2RegisteredClient auth2RegisteredClient = oauth2ClientService.getByClientId(request.getClientId());
        if (auth2RegisteredClient != null && !client.getId().equals(auth2RegisteredClient.getId())) {
            throw new Dify4jForbiddenException("客户端Id已存在");
        }

        client.setClientId(request.getClientId());
        client.setClientSecret("{noop}" + request.getClientSecret());
        client.setClientName(request.getClientName());
        client.setAuthorizationGrantTypes(request.getAuthorizationGrantTypes());
        client.setRedirectUris(request.getRedirectUris());
        client.setScopes(request.getScopes());
        //token设置
        Map<String, Object> settings = new HashMap<>();
        //访问令牌有效期
        settings.put("settings.token.access-token-time-to-live", Duration.ofSeconds(request.getAccessTokenTimeToLive()));

        //如果是刷新模式，则设置默认值
        if (request.getAuthorizationGrantTypes().contains("refresh_token")) {
            //刷新令牌有效期
            settings.put("settings.token.refresh-token-time-to-live", Duration.ofSeconds(request.getRefreshTokenTimeToLive()));
            // 是否重用刷新令牌，默认true
            settings.put("settings.token.reuse-refresh-tokens", false);
        }

        Map<String, Object> finalMap = new HashMap<>();
        finalMap.put("@class", "java.util.Collections$UnmodifiableMap");
        finalMap.putAll(Collections.unmodifiableMap(settings));
        try {
            client.setTokenSettings(objectMapper.writeValueAsString(finalMap));
        } catch (JsonProcessingException e) {
            throw new Dify4jException("无效的token设置: " + e.getMessage());
        }
        client.setClientSettings("{\"@class\":\"java.util.Collections$UnmodifiableMap\",\"settings.client.require-proof-key\":false,\"settings.client.require-authorization-consent\":false}");

        oauth2ClientService.updateOAuth2RegisteredClient(client);
        return ApiResponse.success();
    }

    @Operation(summary = "批量删除OAuth2注册客户端")
    @DeleteMapping()
    public ApiResponse<Void> deleteOAuth2RegisteredClientByIds(@RequestBody List<String> ids) {
        //如果是系统默认的客户端，不允许删除
        OAuth2RegisteredClient difyClient = oauth2ClientService.getByClientId("dify4j");
        if (difyClient != null && ids.contains(difyClient.getId())) {
            throw new Dify4jForbiddenException("系统默认客户端不允许删除");
        }

        oauth2ClientService.deleteOAuth2RegisteredClientByIds(ids);
        return ApiResponse.success();
    }

    private ObjectMapper createObjectMapper() {
        ObjectMapper mapper = new ObjectMapper();
        SimpleModule module = new SimpleModule();
        module.addSerializer(Duration.class, new DurationToArraySerializer());
        mapper.registerModule(module);
        return mapper;
    }

    private class DurationToArraySerializer extends JsonSerializer<Duration> {
        @Override
        public void serialize(Duration duration, JsonGenerator gen, SerializerProvider provider)
                throws IOException {
            gen.writeStartArray();
            gen.writeString("java.time.Duration");  // 类型标记
            gen.writeNumber(duration.getSeconds());  // 秒数
            gen.writeEndArray();
        }
    }

}
