package com.yonchain.ai.plugin.descriptor;

import java.util.List;
import java.util.Map;

/**
 * 提供商描述符
 * 用于描述模型提供商的配置信息（如deepseek.yaml）
 * 
 * @author yonchain
 */
public class ProviderDescriptor {

    /**
     * 背景色
     */
    private String background;

    /**
     * 提供商名称
     */
    private String provider;

    /**
     * 提供商接口类
     */
    private String providerSource;

    /**
     * 配置方法
     */
    private List<String> configurateMethods;

    /**
     * 描述信息（多语言支持）
     */
    private Map<String, String> description;

    /**
     * 标签信息（多语言支持）
     */
    private Map<String, String> label;

    /**
     * 帮助信息
     */
    private HelpConfig help;

    /**
     * 大图标配置
     */
    private Map<String, String> iconLarge;

    /**
     * 小图标配置
     */
    private Map<String, String> iconSmall;

    /**
     * 模型配置
     */
    private Map<String, ModelTypeConfig> models;

    /**
     * 提供商凭证架构
     */
    private ProviderCredentialSchema providerCredentialSchema;

    /**
     * 支持的模型类型
     */
    private List<String> supportedModelTypes;

    // Getters and Setters
    public String getBackground() {
        return background;
    }

    public void setBackground(String background) {
        this.background = background;
    }

    public String getProvider() {
        return provider;
    }

    public void setProvider(String provider) {
        this.provider = provider;
    }

    public String getProviderSource() {
        return providerSource;
    }

    public void setProviderSource(String providerSource) {
        this.providerSource = providerSource;
    }

    public List<String> getConfigurateMethods() {
        return configurateMethods;
    }

    public void setConfigurateMethods(List<String> configurateMethods) {
        this.configurateMethods = configurateMethods;
    }

    public Map<String, String> getDescription() {
        return description;
    }

    public void setDescription(Map<String, String> description) {
        this.description = description;
    }

    public Map<String, String> getLabel() {
        return label;
    }

    public void setLabel(Map<String, String> label) {
        this.label = label;
    }

    public HelpConfig getHelp() {
        return help;
    }

    public void setHelp(HelpConfig help) {
        this.help = help;
    }

    public Map<String, String> getIconLarge() {
        return iconLarge;
    }

    public void setIconLarge(Map<String, String> iconLarge) {
        this.iconLarge = iconLarge;
    }

    public Map<String, String> getIconSmall() {
        return iconSmall;
    }

    public void setIconSmall(Map<String, String> iconSmall) {
        this.iconSmall = iconSmall;
    }

    public Map<String, ModelTypeConfig> getModels() {
        return models;
    }

    public void setModels(Map<String, ModelTypeConfig> models) {
        this.models = models;
    }

    public ProviderCredentialSchema getProviderCredentialSchema() {
        return providerCredentialSchema;
    }

    public void setProviderCredentialSchema(ProviderCredentialSchema providerCredentialSchema) {
        this.providerCredentialSchema = providerCredentialSchema;
    }

    public List<String> getSupportedModelTypes() {
        return supportedModelTypes;
    }

    public void setSupportedModelTypes(List<String> supportedModelTypes) {
        this.supportedModelTypes = supportedModelTypes;
    }

    /**
     * 获取本地化描述
     *
     * @param locale 语言环境
     * @return 本地化描述
     */
    public String getLocalizedDescription(String locale) {
        if (description != null) {
            return description.get(locale);
        }
        return null;
    }

    /**
     * 获取本地化标签
     *
     * @param locale 语言环境
     * @return 本地化标签
     */
    public String getLocalizedLabel(String locale) {
        if (label != null) {
            return label.get(locale);
        }
        return null;
    }

    /**
     * 帮助配置
     */
    public static class HelpConfig {
        private Map<String, String> title;
        private Map<String, String> url;

        public Map<String, String> getTitle() {
            return title;
        }

        public void setTitle(Map<String, String> title) {
            this.title = title;
        }

        public Map<String, String> getUrl() {
            return url;
        }

        public void setUrl(Map<String, String> url) {
            this.url = url;
        }
    }

    /**
     * 模型类型配置
     */
    public static class ModelTypeConfig {
        /**
         * 模型处理器类
         */
        private String source;

        /**
         * 位置配置文件
         */
        private String position;

        /**
         * 预定义模型文件路径
         */
        private List<String> predefined;

        public String getSource() {
            return source;
        }

        public void setSource(String source) {
            this.source = source;
        }

        public String getPosition() {
            return position;
        }

        public void setPosition(String position) {
            this.position = position;
        }

        public List<String> getPredefined() {
            return predefined;
        }

        public void setPredefined(List<String> predefined) {
            this.predefined = predefined;
        }
    }

    /**
     * 提供商凭证架构
     */
    public static class ProviderCredentialSchema {
        private List<CredentialFormSchema> credentialFormSchemas;

        public List<CredentialFormSchema> getCredentialFormSchemas() {
            return credentialFormSchemas;
        }

        public void setCredentialFormSchemas(List<CredentialFormSchema> credentialFormSchemas) {
            this.credentialFormSchemas = credentialFormSchemas;
        }
    }

    /**
     * 凭证表单架构
     */
    public static class CredentialFormSchema {
        private Map<String, String> label;
        private Map<String, String> placeholder;
        private boolean required;
        private String type;
        private String variable;

        public Map<String, String> getLabel() {
            return label;
        }

        public void setLabel(Map<String, String> label) {
            this.label = label;
        }

        public Map<String, String> getPlaceholder() {
            return placeholder;
        }

        public void setPlaceholder(Map<String, String> placeholder) {
            this.placeholder = placeholder;
        }

        public boolean isRequired() {
            return required;
        }

        public void setRequired(boolean required) {
            this.required = required;
        }

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }

        public String getVariable() {
            return variable;
        }

        public void setVariable(String variable) {
            this.variable = variable;
        }
    }

    @Override
    public String toString() {
        return "ProviderDescriptor{" +
                "provider='" + provider + '\'' +
                ", providerSource='" + providerSource + '\'' +
                ", supportedModelTypes=" + supportedModelTypes +
                '}';
    }


}











