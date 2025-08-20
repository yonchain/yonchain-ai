package com.yonchain.ai.model.provider;

import com.yonchain.ai.model.config.ModelProviderConfigLoader;
import com.yonchain.ai.model.entity.AIModel;
import com.yonchain.ai.model.entity.ModelProvider;
import com.yonchain.ai.model.spi.AbstractModelProviderService;
import com.yonchain.ai.model.vo.ModelCapability;
import org.springframework.ai.openai.api.OpenAiApi;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

/**
 * OpenAI模型提供商服务实现
 * 从YAML配置文件中加载模型提供商和模型配置
 */
@Service
public class OpenAIModelProviderService extends AbstractModelProviderService {

    private static final String PROVIDER_CODE = "openai";
    
    private final ModelProviderConfigLoader configLoader;
    private ModelProvider provider;
    private OpenAiApi openAiApi;

    
    @Autowired
    public OpenAIModelProviderService(ModelProviderConfigLoader configLoader) {
        this.configLoader = configLoader;
        // 初始化时加载配置
        loadProviderConfig();
    }
    
    @Autowired(required = false)
    public void setOpenAiApi(OpenAiApi openAiApi) {
        this.openAiApi = openAiApi;
    }

    /**
     * 从配置加载器中加载OpenAI提供商配置
     */
    private void loadProviderConfig() {
        List<ModelProvider> providers = configLoader.loadAllProviders();
        this.provider = providers.stream()
                .filter(p -> PROVIDER_CODE.equals(p.getCode()))
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("未找到OpenAI提供商配置"));
    }

    @Override
    protected void doInitialize(Map<String, Object> config) {
        // 在实际应用中，这里应该使用配置参数初始化OpenAI客户端
        // 例如：设置API密钥、代理等
    }

    @Override
    public ModelProvider getProviderInfo() {
        return provider;
    }

    @Override
    public List<AIModel> listModels() {
        return provider.getModels();
    }

    @Override
    public AIModel getModel(String modelCode) {
        return listModels().stream()
                .filter(model -> model.getCode().equals(modelCode))
                .findFirst()
                .orElse(null);
    }

    @Override
    public List<ModelCapability> getModelCapabilities(String modelCode) {
        AIModel model = getModel(modelCode);
        if (model == null) {
            return Collections.emptyList();
        }
        
        // 从模型的capabilities字段获取能力代码列表
        List<String> capabilityCodes = Arrays.asList(model.getCapabilities());
        
        // 从提供商的capabilities映射中获取对应的能力对象
        return capabilityCodes.stream()
                .map(code -> provider.getCapabilities().get(code))
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    @Override
    public boolean validateProviderConfig(Map<String, Object> config) {
        // 验证提供商配置是否有效
        return config != null && config.containsKey("apiKey") && config.get("apiKey") != null;
    }

    @Override
    public boolean validateModelConfig(String modelCode, Map<String, Object> config) {
        // 验证模型配置是否有效
        return true; // 简单实现，实际应用中应该根据模型类型进行验证
    }
}