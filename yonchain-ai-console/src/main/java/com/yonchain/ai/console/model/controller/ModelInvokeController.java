package com.yonchain.ai.console.model.controller;

import com.yonchain.ai.api.model.ModelInvokeService;
import com.yonchain.ai.console.model.request.CompletionRequest;
import com.yonchain.ai.console.model.request.ImageGenerationRequest;
import com.yonchain.ai.console.model.response.CompletionResponse;
import com.yonchain.ai.console.model.response.ImageGenerationResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

/**
 * 模型调用控制器
 */
@RestController
@RequestMapping("/api/v1/models/invoke")
@Tag(name = "模型调用", description = "模型调用相关接口")
public class ModelInvokeController {

/*    @Autowired
    private ModelInvokeService modelInvokeService;*/

/*
    @PostMapping("/{modelCode}/chat")
    @Operation(summary = "聊天完成（Chat Completion）")
    public ResponseEntity<ChatCompletionResponse> chatCompletion(
            @Parameter(description = "模型代码") @PathVariable String modelCode,
            @RequestBody ChatCompletionRequest request) {
        return ResponseEntity.ok(modelInvokeService.chatCompletion(modelCode, request));
    }

    @PostMapping(value = "/{modelCode}/chat/stream", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    @Operation(summary = "聊天完成（Chat Completion）- 流式输出")
    public SseEmitter chatCompletionStream(
            @Parameter(description = "模型代码") @PathVariable String modelCode,
            @RequestBody ChatCompletionRequest request) {
        return modelInvokeService.chatCompletionStream(modelCode, request);
    }
*/
/*
    @PostMapping("/{modelCode}/completions")
    @Operation(summary = "文本完成（Text Completion）")
    public ResponseEntity<CompletionResponse> textCompletion(
            @Parameter(description = "模型代码") @PathVariable String modelCode,
            @RequestBody CompletionRequest request) {
        return ResponseEntity.ok(modelInvokeService.textCompletion(modelCode, request));
    }

    @PostMapping(value = "/{modelCode}/completions/stream", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    @Operation(summary = "文本完成（Text Completion）- 流式输出")
    public SseEmitter textCompletionStream(
            @Parameter(description = "模型代码") @PathVariable String modelCode,
            @RequestBody CompletionRequest request) {
        return modelInvokeService.textCompletionStream(modelCode, request);
    }

    @PostMapping("/{modelCode}/images")
    @Operation(summary = "图像生成（Image Generation）")
    public ResponseEntity<ImageGenerationResponse> generateImage(
            @Parameter(description = "模型代码") @PathVariable String modelCode,
            @RequestBody ImageGenerationRequest request) {
        return ResponseEntity.ok(modelInvokeService.generateImage(modelCode, request));
    }

    @PostMapping("/{modelCode}/embeddings")
    @Operation(summary = "文本嵌入（Text Embedding）")
    public ResponseEntity<EmbeddingResponse> createEmbedding(
            @Parameter(description = "模型代码") @PathVariable String modelCode,
            @RequestBody EmbeddingRequest request) {
        return ResponseEntity.ok(modelInvokeService.createEmbedding(modelCode, request));
    }*/
}