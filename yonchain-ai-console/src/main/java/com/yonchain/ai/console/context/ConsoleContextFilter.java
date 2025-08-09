package com.yonchain.ai.console.context;


import com.yonchain.ai.api.sys.*;
import com.yonchain.ai.api.sys.enums.RoleType;
import com.yonchain.ai.api.security.SecurityService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.yonchain.ai.web.response.ApiErrorResponse;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * 从 Spring Security 中提取用户信息并存入 UserContextHolder
 */
public class ConsoleContextFilter extends OncePerRequestFilter {

    private static final Logger log = LoggerFactory.getLogger(ConsoleContextFilter.class);

    private final SecurityService securityService;
    private final ObjectMapper objectMapper;
    private final IdmCacheService idmCacheService;
    private final UserService userService; // 用户服务
    private final TenantService tenantService; // 租户服务

    public ConsoleContextFilter(SecurityService securityService,
                                IdmCacheService idmCacheService,
                                ObjectMapper objectMapper,
                                UserService userService,
                                TenantService tenantService) {
        this.securityService = securityService;
        this.objectMapper = objectMapper;
        this.idmCacheService = idmCacheService;
        this.userService = userService;
        this.tenantService = tenantService;
    }

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain) throws ServletException, IOException {
        try {
            String token = request.getHeader("Authorization");
            if (!StringUtils.hasText(token) || !token.startsWith("Bearer ")) {
                filterChain.doFilter(request, response);
                return;
            }
            token = token.substring(7);
            
            // 获取并验证用户信息
            String userId = securityService.getUserIdFromToken(token);
            User user = idmCacheService.getUserById(userId);
            if (user == null) {
                user = userService.getUserById(userId);
                if (user == null) {
                    writeErrorResponse(response, new ApiErrorResponse(HttpStatus.BAD_REQUEST.value(),
                            "OBJECT_NOT_FOUND",
                            "Token的用户不存在"));
                    return;
                }
                idmCacheService.cacheUser(user);
            }

            // 获取租户信息
            Tenant tenant = idmCacheService.getTenantByUserId(userId);
            if (tenant == null) {
                tenant = tenantService.getCurrentTenantByUserId(userId);
                if (tenant == null) {
                    writeErrorResponse(response, new ApiErrorResponse(HttpStatus.BAD_REQUEST.value(),
                            "OBJECT_NOT_FOUND",
                            "获取当前用户的租户为空"));
                    return;
                }
                idmCacheService.cacheTenant(userId, tenant);
            }

            // 批量获取并处理角色信息
            List<Role> roles = idmCacheService.getUserRoles(tenant.getId(), user.getId());
            if (CollectionUtils.isEmpty(roles)) {
                roles = userService.getUserRoles(tenant.getId(), userId);
                if (CollectionUtils.isEmpty(roles)) {
                    writeErrorResponse(response, new ApiErrorResponse(HttpStatus.BAD_REQUEST.value(),
                            "OBJECT_NOT_FOUND",
                            "获取当前用户的角色为空"));
                    return;
                }
                idmCacheService.cacheUserRoles(tenant.getId(), userId, roles);
            }

            List<String> roleIds = new ArrayList<>();
            List<String> roleCodes = new ArrayList();
            AtomicBoolean isSuperAdmin = new AtomicBoolean(false);

            roles.forEach(role -> {
                roleIds.add(role.getId());
                roleCodes.add(role.getCode());
                if (RoleType.OWNER.name().equalsIgnoreCase(role.getCode())) {
                    isSuperAdmin.set(true);
                }
            });
            CurrentUser currentUser = new CurrentUser(userId,
                    user.getName(),
                    user.getEmail(),
                    tenant.getId(),roleIds, roleCodes, isSuperAdmin.get());

            // 设置上下文并继续过滤器链
            ConsoleContext context = new ConsoleContext();
            context.setCurrentUser(currentUser);
            ConsoleContextHolder.setContext(context);

            filterChain.doFilter(request, response);
        } catch (Exception e) {
            log.error("Filter processing error", e);
            writeErrorResponse(response, 
                new ApiErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR.value(), 
                    "INTERNAL_ERROR", "服务器内部错误"));
        } finally {
            ConsoleContextHolder.clearContext();
        }
    }

    private void writeErrorResponse(HttpServletResponse response, ApiErrorResponse errorResponse) 
            throws IOException {
        response.setStatus(errorResponse.status());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        objectMapper.writeValue(response.getOutputStream(), errorResponse);
    }

}
