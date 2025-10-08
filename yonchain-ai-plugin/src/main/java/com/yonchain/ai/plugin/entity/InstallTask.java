package com.yonchain.ai.plugin.entity;

import lombok.Data;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * 安装任务实体
 * 对应 install_tasks 表
 * 
 * @author yonchain
 */
@Data
public class InstallTask {
    
    /**
     * 主键ID
     */
    private String id;

    /**
     * 租户ID
     */
    private String tenantId;

    /**
     * 任务状态：pending, running, completed, failed
     */
    private String status;

    /**
     * 总插件数量
     */
    private Long totalPlugins;

    /**
     * 已完成插件数量
     */
    private Long completedPlugins;

    /**
     * 插件列表（JSON格式）
     */
    private String plugins;

    /**
     * 错误信息
     */
    private String errorMessage;

    /**
     * 创建时间
     */
    private LocalDateTime createdAt;

    /**
     * 更新时间
     */
    private LocalDateTime updatedAt;

    /**
     * 构造函数
     */
    public InstallTask() {
        this.totalPlugins = 0L;
        this.completedPlugins = 0L;
    }

    /**
     * 创建新的安装任务
     */
    public static InstallTask create(String tenantId, String plugins, Long totalPlugins) {
        InstallTask task = new InstallTask();
        task.setId(UUID.randomUUID().toString());
        task.setTenantId(tenantId);
        task.setStatus("pending");
        task.setPlugins(plugins);
        task.setTotalPlugins(totalPlugins);
        task.setCompletedPlugins(0L);
        task.setCreatedAt(LocalDateTime.now());
        task.setUpdatedAt(LocalDateTime.now());
        return task;
    }

    /**
     * 更新任务状态
     */
    public void updateStatus(String status) {
        this.status = status;
        this.updatedAt = LocalDateTime.now();
    }

    /**
     * 更新进度
     */
    public void updateProgress(Long completedPlugins) {
        this.completedPlugins = completedPlugins;
        this.updatedAt = LocalDateTime.now();
    }

    /**
     * 设置错误信息
     */
    public void setError(String errorMessage) {
        this.status = "failed";
        this.errorMessage = errorMessage;
        this.updatedAt = LocalDateTime.now();
    }

    /**
     * 完成任务
     */
    public void complete() {
        this.status = "completed";
        this.completedPlugins = this.totalPlugins;
        this.updatedAt = LocalDateTime.now();
    }

    /**
     * 检查任务是否完成
     */
    public boolean isCompleted() {
        return "completed".equals(this.status);
    }

    /**
     * 检查任务是否失败
     */
    public boolean isFailed() {
        return "failed".equals(this.status);
    }

    /**
     * 检查任务是否正在运行
     */
    public boolean isRunning() {
        return "running".equals(this.status);
    }

    /**
     * 获取进度百分比
     */
    public double getProgressPercentage() {
        if (totalPlugins == null || totalPlugins == 0) {
            return 0.0;
        }
        return (double) (completedPlugins != null ? completedPlugins : 0) / totalPlugins * 100.0;
    }
}


