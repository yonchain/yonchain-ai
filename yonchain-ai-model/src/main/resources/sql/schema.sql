-- 模型提供商表
CREATE TABLE IF NOT EXISTS ai_model_provider (
    id BIGSERIAL PRIMARY KEY,
    code VARCHAR(50) NOT NULL UNIQUE,
    name VARCHAR(100) NOT NULL,
    description TEXT,
    icon_url VARCHAR(255),
    website_url VARCHAR(255),
    type VARCHAR(20) NOT NULL,
    enabled BOOLEAN DEFAULT TRUE,
    sort_order INT DEFAULT 0,
    api_key VARCHAR(255),
    base_url VARCHAR(255),
    proxy_url VARCHAR(255),
    config JSONB,
    config_schema JSONB,
    supported_model_types JSONB,
    create_time TIMESTAMP NOT NULL,
    update_time TIMESTAMP NOT NULL
);

-- 模型表
CREATE TABLE IF NOT EXISTS ai_model (
    id BIGSERIAL PRIMARY KEY,
    code VARCHAR(50) NOT NULL UNIQUE,
    name VARCHAR(100) NOT NULL,
    description TEXT,
    icon_url VARCHAR(255),
    model_type VARCHAR(20) NOT NULL,
    provider_id BIGINT NOT NULL,
    provider_code VARCHAR(50) NOT NULL,
    version VARCHAR(20),
    is_system BOOLEAN DEFAULT FALSE,
    enabled BOOLEAN DEFAULT TRUE,
    sort_order INT DEFAULT 0,
    config JSONB,
    config_schema JSONB,
    capabilities TEXT[],
    create_time TIMESTAMP NOT NULL,
    update_time TIMESTAMP NOT NULL,
    FOREIGN KEY (provider_id) REFERENCES ai_model_provider(id)
);

-- 创建索引
CREATE INDEX IF NOT EXISTS idx_model_provider_id ON ai_model(provider_id);
CREATE INDEX IF NOT EXISTS idx_model_provider_code ON ai_model(provider_code);
CREATE INDEX IF NOT EXISTS idx_model_type ON ai_model(model_type);