package com.yonchain.ai.model.controller;

import com.yonchain.ai.model.entity.AIModel;
import com.yonchain.ai.model.entity.ModelProvider;
import com.yonchain.ai.model.service.ModelManagerService;
import com.yonchain.ai.model.vo.ModelCapability;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * 模型管理控制器
 */
@RestController
@RequestMapping("/api/v1/models")
@Tag(name = "模型管理", description = "模型管理相关接口")
public class ModelManagerController {

    @Autowired
    private ModelManagerService modelManagerService;

    @GetMapping("/providers")
    @Operation(summary = "获取所有模型提供商列表")
    public ResponseEntity<List<ModelProvider>> listProviders() {
        return ResponseEntity.ok(modelManagerService.listProviders());
    }

    @GetMapping("/providers/{providerCode}")
    @Operation(summary = "获取指定提供商信息")
    public ResponseEntity<ModelProvider> getProvider(
            @Parameter(description = "提供商代码") @PathVariable String providerCode) {
        return ResponseEntity.ok(modelManagerService.getProvider(providerCode));
    }

    @GetMapping("/providers/{providerCode}/models")
    @Operation(summary = "获取指定提供商的所有模型列表")
    public ResponseEntity<List<AIModel>> listModelsByProvider(
            @Parameter(description = "提供商代码") @PathVariable String providerCode) {
        return ResponseEntity.ok(modelManagerService.listModelsByProvider(providerCode));
    }

    @GetMapping
    @Operation(summary = "获取所有模型列表")
    public ResponseEntity<List<AIModel>> listAllModels() {
        return ResponseEntity.ok(modelManagerService.listAllModels());
    }

    @GetMapping("/{modelCode}")
    @Operation(summary = "获取指定模型信息")
    public ResponseEntity<AIModel> getModel(
            @Parameter(description = "模型代码") @PathVariable String modelCode) {
        return ResponseEntity.ok(modelManagerService.getModel(modelCode));
    }

    @PostMapping("/providers")
    @Operation(summary = "添加自定义模型提供商")
    public ResponseEntity<ModelProvider> addProvider(
            @RequestBody ModelProvider provider,
            @RequestParam Map<String, Object> config) {
        return ResponseEntity.ok(modelManagerService.addProvider(provider, config));
    }

    @PutMapping("/providers/{providerCode}")
    @Operation(summary = "更新模型提供商")
    public ResponseEntity<ModelProvider> updateProvider(
            @Parameter(description = "提供商代码") @PathVariable String providerCode,
            @RequestBody ModelProvider provider,
            @RequestParam Map<String, Object> config) {
        provider.setCode(providerCode);
        return ResponseEntity.ok(modelManagerService.updateProvider(provider, config));
    }

    @DeleteMapping("/providers/{providerCode}")
    @Operation(summary = "删除模型提供商")
    public ResponseEntity<Boolean> deleteProvider(
            @Parameter(description = "提供商代码") @PathVariable String providerCode) {
        return ResponseEntity.ok(modelManagerService.deleteProvider(providerCode));
    }

    @PostMapping
    @Operation(summary = "添加自定义模型")
    public ResponseEntity<AIModel> addModel(
            @RequestBody AIModel model,
            @RequestParam Map<String, Object> config) {
        return ResponseEntity.ok(modelManagerService.addModel(model, config));
    }

    @PutMapping("/{modelCode}")
    @Operation(summary = "更新模型")
    public ResponseEntity<AIModel> updateModel(
            @Parameter(description = "模型代码") @PathVariable String modelCode,
            @RequestBody AIModel model,
            @RequestParam Map<String, Object> config) {
        model.setCode(modelCode);
        return ResponseEntity.ok(modelManagerService.updateModel(model, config));
    }

    @DeleteMapping("/{modelCode}")
    @Operation(summary = "删除模型")
    public ResponseEntity<Boolean> deleteModel(
            @Parameter(description = "模型代码") @PathVariable String modelCode) {
        return ResponseEntity.ok(modelManagerService.deleteModel(modelCode));
    }

    @GetMapping("/{modelCode}/capabilities")
    @Operation(summary = "获取模型能力列表")
    public ResponseEntity<List<ModelCapability>> getModelCapabilities(
            @Parameter(description = "模型代码") @PathVariable String modelCode) {
        return ResponseEntity.ok(modelManagerService.getModelCapabilities(modelCode));
    }

    @GetMapping("/types/{modelType}")
    @Operation(summary = "获取指定类型的所有模型")
    public ResponseEntity<List<AIModel>> listModelsByType(
            @Parameter(description = "模型类型") @PathVariable String modelType) {
        return ResponseEntity.ok(modelManagerService.listModelsByType(modelType));
    }

    @PutMapping("/providers/{providerCode}/enable")
    @Operation(summary = "启用或禁用模型提供商")
    public ResponseEntity<ModelProvider> enableProvider(
            @Parameter(description = "提供商代码") @PathVariable String providerCode,
            @Parameter(description = "是否启用") @RequestParam boolean enabled) {
        return ResponseEntity.ok(modelManagerService.enableProvider(providerCode, enabled));
    }

    @PutMapping("/{modelCode}/enable")
    @Operation(summary = "启用或禁用模型")
    public ResponseEntity<AIModel> enableModel(
            @Parameter(description = "模型代码") @PathVariable String modelCode,
            @Parameter(description = "是否启用") @RequestParam boolean enabled) {
        return ResponseEntity.ok(modelManagerService.enableModel(modelCode, enabled));
    }
}