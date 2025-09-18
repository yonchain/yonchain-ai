package com.yonchain.ai.plugin.validation;

import java.util.ArrayList;
import java.util.List;

/**
 * 插件验证结果
 * 
 * @author yonchain
 */
public class ValidationResult {
    
    private final List<String> errors = new ArrayList<>();
    private final List<String> warnings = new ArrayList<>();
    
    /**
     * 添加错误信息
     * 
     * @param error 错误信息
     */
    public void addError(String error) {
        if (error != null && !error.trim().isEmpty()) {
            errors.add(error);
        }
    }
    
    /**
     * 添加警告信息
     * 
     * @param warning 警告信息
     */
    public void addWarning(String warning) {
        if (warning != null && !warning.trim().isEmpty()) {
            warnings.add(warning);
        }
    }
    
    /**
     * 是否有错误
     * 
     * @return 是否有错误
     */
    public boolean hasErrors() {
        return !errors.isEmpty();
    }
    
    /**
     * 是否有警告
     * 
     * @return 是否有警告
     */
    public boolean hasWarnings() {
        return !warnings.isEmpty();
    }
    
    /**
     * 验证是否通过（无错误）
     * 
     * @return 是否通过
     */
    public boolean isValid() {
        return !hasErrors();
    }
    
    /**
     * 获取所有错误信息
     * 
     * @return 错误信息列表
     */
    public List<String> getErrors() {
        return new ArrayList<>(errors);
    }
    
    /**
     * 获取所有警告信息
     * 
     * @return 警告信息列表
     */
    public List<String> getWarnings() {
        return new ArrayList<>(warnings);
    }
    
    /**
     * 获取错误数量
     * 
     * @return 错误数量
     */
    public int getErrorCount() {
        return errors.size();
    }
    
    /**
     * 获取警告数量
     * 
     * @return 警告数量
     */
    public int getWarningCount() {
        return warnings.size();
    }
    
    /**
     * 清空所有验证结果
     */
    public void clear() {
        errors.clear();
        warnings.clear();
    }
    
    /**
     * 合并其他验证结果
     * 
     * @param other 其他验证结果
     */
    public void merge(ValidationResult other) {
        if (other != null) {
            this.errors.addAll(other.errors);
            this.warnings.addAll(other.warnings);
        }
    }
    
    /**
     * 获取第一个错误信息
     * 
     * @return 第一个错误信息，如果没有错误则返回null
     */
    public String getFirstError() {
        return errors.isEmpty() ? null : errors.get(0);
    }
    
    /**
     * 获取错误信息（多个错误用换行符连接）
     * 
     * @return 错误信息字符串
     */
    public String getErrorMessage() {
        if (errors.isEmpty()) {
            return null;
        }
        return String.join("\n", errors);
    }
    
    /**
     * 获取第一个警告信息
     * 
     * @return 第一个警告信息，如果没有警告则返回null
     */
    public String getFirstWarning() {
        return warnings.isEmpty() ? null : warnings.get(0);
    }
    
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("ValidationResult{");
        sb.append("valid=").append(isValid());
        sb.append(", errors=").append(errors.size());
        sb.append(", warnings=").append(warnings.size());
        
        if (hasErrors()) {
            sb.append(", errorDetails=").append(errors);
        }
        
        if (hasWarnings()) {
            sb.append(", warningDetails=").append(warnings);
        }
        
        sb.append('}');
        return sb.toString();
    }
}