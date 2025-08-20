package com.yonchain.ai.model.controller;


import com.yonchain.ai.model.dto.ChatCompletionRequest;
import com.yonchain.ai.model.dto.ChatCompletionResponse;
import com.yonchain.ai.model.service.ChatService;
import com.yonchain.ai.model.service.ModelInvokeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;


/**
 * 聊天控制器
 *
 * @author Cgy
 */
@RequestMapping
@RestController
public class ChatController {

    @Autowired
    private ChatService chatService;

    @PostMapping("/models/{modelCode}/chat")
    @Operation(summary = "聊天完成（Chat Completion）")
    public ResponseEntity<ChatCompletionResponse> chatCompletion(
            @Parameter(description = "模型代码") @PathVariable String modelCode,
            @RequestBody ChatCompletionRequest request) {
        return ResponseEntity.ok(chatService.chatCompletion(modelCode, request));
    }

    @PostMapping(value = "/models/{modelCode}/chat/stream", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    @Operation(summary = "聊天完成（Chat Completion）- 流式输出")
    public SseEmitter chatCompletionStream(
            @Parameter(description = "模型代码") @PathVariable String modelCode,
            @RequestBody ChatCompletionRequest request) {
        return chatService.chatCompletionStream(modelCode, request);
    }

}
