-- =====================================================
-- 为model_provider表添加plugin_id字段
-- =====================================================

-- 添加plugin_id字段
ALTER TABLE model_provider 
ADD COLUMN plugin_id VARCHAR(100);

-- 添加索引以提高查询性能
CREATE INDEX idx_model_provider_plugin_id ON model_provider(plugin_id);

-- 添加字段注释
COMMENT ON COLUMN model_provider.plugin_id IS '插件ID，标识该提供商来自哪个插件';
