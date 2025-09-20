-- =====================================================
-- 为model_provider表添加元数据字段
-- =====================================================

-- 添加提供商元数据字段
ALTER TABLE model_provider 
ADD COLUMN name VARCHAR(255),
ADD COLUMN description TEXT,
ADD COLUMN icon VARCHAR(500),
ADD COLUMN background VARCHAR(50),
ADD COLUMN sort_order INT DEFAULT 100,
ADD COLUMN supported_model_types TEXT,
ADD COLUMN configurate_methods TEXT,
ADD COLUMN help_info TEXT,
ADD COLUMN credential_schema TEXT;

-- 添加索引以提高查询性能
CREATE INDEX idx_model_provider_sort_order ON model_provider(sort_order);
CREATE INDEX idx_model_provider_name ON model_provider(name);

-- 添加字段注释
COMMENT ON COLUMN model_provider.name IS '提供商显示名称';
COMMENT ON COLUMN model_provider.description IS '提供商描述';
COMMENT ON COLUMN model_provider.icon IS '提供商图标路径';
COMMENT ON COLUMN model_provider.background IS '背景颜色';
COMMENT ON COLUMN model_provider.sort_order IS '排序权重，数值越小排序越靠前';
COMMENT ON COLUMN model_provider.supported_model_types IS '支持的模型类型列表，JSON格式存储';
COMMENT ON COLUMN model_provider.configurate_methods IS '配置方法列表，JSON格式存储';
COMMENT ON COLUMN model_provider.help_info IS '帮助信息，JSON格式存储';
COMMENT ON COLUMN model_provider.credential_schema IS '提供商凭证配置Schema，JSON格式存储';
