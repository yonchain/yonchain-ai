-- =====================================================
-- 插件系统数据库表结构
-- =====================================================

-- 1. 插件信息主表
CREATE TABLE IF NOT EXISTS plugin_info (
    plugin_id VARCHAR(100) PRIMARY KEY,
    name VARCHAR(200) NOT NULL,
    version VARCHAR(50) NOT NULL,
    description VARCHAR(1000),
    author VARCHAR(100),
    homepage VARCHAR(500),
    type VARCHAR(20) NOT NULL DEFAULT 'MODEL',
    status VARCHAR(20) NOT NULL DEFAULT 'DISABLED',
    plugin_path VARCHAR(1000),
    main_class VARCHAR(500),
    provider_source VARCHAR(500),
    provider_interface VARCHAR(500),
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    installed_at TIMESTAMP NULL,
    enabled_at TIMESTAMP NULL,
    disabled_at TIMESTAMP NULL
);

-- 添加表和字段注释
COMMENT ON TABLE plugin_info IS '插件信息主表';
COMMENT ON COLUMN plugin_info.plugin_id IS '插件ID';
COMMENT ON COLUMN plugin_info.name IS '插件名称';
COMMENT ON COLUMN plugin_info.version IS '插件版本';
COMMENT ON COLUMN plugin_info.description IS '插件描述';
COMMENT ON COLUMN plugin_info.author IS '插件作者';
COMMENT ON COLUMN plugin_info.homepage IS '插件主页';
COMMENT ON COLUMN plugin_info.type IS '插件类型：MODEL, TOOL, UI, EXTENSION';
COMMENT ON COLUMN plugin_info.status IS '插件状态';
COMMENT ON COLUMN plugin_info.plugin_path IS '插件路径';
COMMENT ON COLUMN plugin_info.main_class IS '主类名';
COMMENT ON COLUMN plugin_info.provider_source IS '提供者源类';
COMMENT ON COLUMN plugin_info.provider_interface IS '提供者接口';
COMMENT ON COLUMN plugin_info.created_at IS '创建时间';
COMMENT ON COLUMN plugin_info.updated_at IS '更新时间';
COMMENT ON COLUMN plugin_info.installed_at IS '安装时间';
COMMENT ON COLUMN plugin_info.enabled_at IS '启用时间';
COMMENT ON COLUMN plugin_info.disabled_at IS '禁用时间';

-- 创建索引
CREATE INDEX idx_plugin_type ON plugin_info(type);
CREATE INDEX idx_plugin_status ON plugin_info(status);
CREATE INDEX idx_plugin_created_at ON plugin_info(created_at);

-- 创建更新时间自动触发器函数
CREATE OR REPLACE FUNCTION update_updated_at_column()
RETURNS TRIGGER AS $$
BEGIN
    NEW.updated_at = CURRENT_TIMESTAMP;
    RETURN NEW;
END;
$$ language 'plpgsql';

-- 为plugin_info表创建更新触发器
CREATE TRIGGER update_plugin_info_updated_at 
    BEFORE UPDATE ON plugin_info 
    FOR EACH ROW 
    EXECUTE FUNCTION update_updated_at_column();

-- 2. 插件依赖表
CREATE TABLE IF NOT EXISTS plugin_dependencies (
    id BIGSERIAL PRIMARY KEY,
    plugin_id VARCHAR(100) NOT NULL,
    dependency_id VARCHAR(100) NOT NULL,
    min_version VARCHAR(50),
    max_version VARCHAR(50),
    optional BOOLEAN NOT NULL DEFAULT FALSE,
    FOREIGN KEY (plugin_id) REFERENCES plugin_info(plugin_id) ON DELETE CASCADE
);

-- 添加表和字段注释
COMMENT ON TABLE plugin_dependencies IS '插件依赖表';
COMMENT ON COLUMN plugin_dependencies.id IS '主键ID';
COMMENT ON COLUMN plugin_dependencies.plugin_id IS '插件ID';
COMMENT ON COLUMN plugin_dependencies.dependency_id IS '依赖的插件ID';
COMMENT ON COLUMN plugin_dependencies.min_version IS '最小版本';
COMMENT ON COLUMN plugin_dependencies.max_version IS '最大版本';
COMMENT ON COLUMN plugin_dependencies.optional IS '是否可选依赖';

-- 创建索引
CREATE INDEX idx_plugin_dep_plugin_id ON plugin_dependencies(plugin_id);
CREATE INDEX idx_plugin_dep_dependency_id ON plugin_dependencies(dependency_id);

-- 3. 插件扩展点表
CREATE TABLE IF NOT EXISTS plugin_extensions (
    id BIGSERIAL PRIMARY KEY,
    plugin_id VARCHAR(100) NOT NULL,
    point VARCHAR(200) NOT NULL,
    implementation VARCHAR(500) NOT NULL,
    FOREIGN KEY (plugin_id) REFERENCES plugin_info(plugin_id) ON DELETE CASCADE
);

-- 添加表和字段注释
COMMENT ON TABLE plugin_extensions IS '插件扩展点表';
COMMENT ON COLUMN plugin_extensions.id IS '主键ID';
COMMENT ON COLUMN plugin_extensions.plugin_id IS '插件ID';
COMMENT ON COLUMN plugin_extensions.point IS '扩展点名称';
COMMENT ON COLUMN plugin_extensions.implementation IS '实现类';

-- 创建索引
CREATE INDEX idx_plugin_ext_plugin_id ON plugin_extensions(plugin_id);
CREATE INDEX idx_plugin_ext_point ON plugin_extensions(point);

-- 4. 插件服务表
CREATE TABLE IF NOT EXISTS plugin_services (
    id BIGSERIAL PRIMARY KEY,
    plugin_id VARCHAR(100) NOT NULL,
    service_name VARCHAR(200) NOT NULL,
    service_class VARCHAR(500) NOT NULL,
    FOREIGN KEY (plugin_id) REFERENCES plugin_info(plugin_id) ON DELETE CASCADE
);

-- 添加表和字段注释
COMMENT ON TABLE plugin_services IS '插件服务表';
COMMENT ON COLUMN plugin_services.id IS '主键ID';
COMMENT ON COLUMN plugin_services.plugin_id IS '插件ID';
COMMENT ON COLUMN plugin_services.service_name IS '服务名称';
COMMENT ON COLUMN plugin_services.service_class IS '服务类';

-- 创建索引
CREATE INDEX idx_plugin_svc_plugin_id ON plugin_services(plugin_id);
CREATE INDEX idx_plugin_svc_name ON plugin_services(service_name);

-- =====================================================
-- 插件状态枚举值说明
-- =====================================================
-- DISABLED: 已安装但禁用
-- ENABLED: 已安装且启用
-- ENABLING: 启用中
-- DISABLING: 禁用中
-- UNINSTALLING: 卸载中
-- ERROR: 错误状态

-- =====================================================
-- 插件类型枚举值说明
-- =====================================================
-- MODEL: 模型插件
-- TOOL: 工具插件
-- UI: 用户界面插件
-- EXTENSION: 扩展插件

-- =====================================================
-- 初始化数据（可选）
-- =====================================================

-- 插入示例插件数据（开发测试用）
-- INSERT INTO plugin_info (plugin_id, name, version, description, author, type, status) 
-- VALUES ('deepseek-plugin', 'DeepSeek AI Plugin', '1.0.0', 'DeepSeek AI模型插件', 'YonChain', 'MODEL', 'DISABLED');

COMMIT;






