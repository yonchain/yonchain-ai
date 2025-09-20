package com.yonchain.ai.tmpl.service;

import com.yonchain.ai.api.exception.YonchainException;
import com.yonchain.ai.api.model.*;
import com.yonchain.ai.model.factory.ModelFactory;
import com.yonchain.ai.model.registry.ModelRegistry;
import com.yonchain.ai.tmpl.mapper.ModelMapper;
import com.yonchain.ai.tmpl.mapper.ModelProviderMapper;
import com.yonchain.ai.util.IdUtil;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import lombok.extern.slf4j.Slf4j;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.core.type.TypeReference;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Collectors;

/**
 * 模型服务实现类
 */
@Slf4j
@Service
public class ModelServiceImpl implements ModelService {

    @Autowired(required = false)
    private ModelMapper modelMapper;

    @Autowired(required = false)
    private ModelProviderMapper modelProviderMapper;


    @Override
    public ModelInfo getModelById(String id) {
        return null;
    }

    @Override
    public List<ModelInfo> getModels(String tenantId, Map<String, Object> queryParam) {
        return List.of();
    }

    @Override
    public List<ModelProvider> getProviders(String tenantId, Map<String, Object> queryParam) {
        return List.of();
    }

    @Override
    public ProviderConfigResponse getProviderConfig(String tenantId, String providerCode) {
        return null;
    }

    @Override
    public ModelInfo getModel(String provider, String modelCode) {
        return null;
    }

    @Override
    public void saveProviderConfig(String tenantId, String providerCode, Map<String, Object> config) {

    }

    @Override
    public void updateModelStatus(String tenantId, String provider, String modelCode, boolean enabled) {

    }

    @Override
    public void saveModelConfig(String tenantId, ModelInfo modelInfo) {

    }
}
