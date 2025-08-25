package com.yonchain.ai.console.model.controller;


import com.yonchain.ai.api.model.ChatCompletionRequest;
import com.yonchain.ai.api.model.ChatCompletionResponse;
import com.yonchain.ai.api.model.ChatService;
import com.yonchain.ai.console.BaseController;
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
public class ChatController extends BaseController {

    @Autowired
    private ChatService chatService;

    @PostMapping("/models/{modelCode}/chat")
    @Operation(summary = "聊天完成（Chat Completion）")
    public ResponseEntity<ChatCompletionResponse> chatCompletion(
            @Parameter(description = "模型代码") @PathVariable String modelCode,
            @RequestBody ChatCompletionRequest request) {
        String tenantId = this.getCurrentTenantId();
        ChatCompletionResponse response = chatService.chatCompletion(tenantId, modelCode, request);
        return ResponseEntity.ok(response);
    }

    @PostMapping(value = "/models/{modelCode}/chat/stream", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    @Operation(summary = "聊天完成（Chat Completion）- 流式输出")
    public SseEmitter chatCompletionStream(
            @Parameter(description = "模型代码") @PathVariable String modelCode,
            @RequestBody ChatCompletionRequest request) {
        //TODO 待开发
        return null;//chatService.chatCompletionStream(modelCode, request);
    }

}
