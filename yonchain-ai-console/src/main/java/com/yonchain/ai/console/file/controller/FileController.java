package com.yonchain.ai.console.file.controller;

import com.yonchain.ai.api.common.Page;
import com.yonchain.ai.api.exception.YonchainException;
import com.yonchain.ai.api.exception.YonchainIllegalStateException;
import com.yonchain.ai.api.exception.YonchainResourceNotFoundException;
import com.yonchain.ai.api.storage.StorageService;
import com.yonchain.ai.console.BaseController;
import com.yonchain.ai.console.file.entity.FileEntity;
import com.yonchain.ai.console.file.request.FileQueryRequest;
import com.yonchain.ai.console.file.response.FileResponse;
import com.yonchain.ai.console.file.service.FileService;
import com.yonchain.ai.web.response.ApiResponse;
import com.yonchain.ai.web.response.PageResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.*;

/**
 * 文件控制器
 *
 * @author Cgy
 * @since 1.0.0
 */
@RestController
@RequestMapping("/files")
@Tag(name = "文件管理", description = "文件管理相关接口")
public class FileController extends BaseController {

    // 支持的文件类型
    private static final Set<String> RANGE_SUPPORTED_MIME_TYPES = new HashSet<>(Arrays.asList(
            "audio/mpeg", "audio/wav", "audio/mp4", "audio/ogg ", "audio/flac", "audio/aac",
            "video/mp4", "video/webm", "video/quicktime", "audio/x-m4a"
    ));

    @Autowired
    private FileService fileService;

    @Autowired
    private StorageService storageService;

    /**
     * 获取文件
     *
     * @param id 文件ID
     * @return 文件信息
     */
    @Operation(summary = "获取文件详情", description = "根据ID获取文件详细信息")
    @GetMapping("/{id}")
    public FileResponse getFileById(@Parameter(description = "文件ID") @PathVariable String id) {
        FileEntity file = fileService.getFileById(id);
        if (file == null) {
            throw new YonchainResourceNotFoundException("文件不存在");
        }
        return responseFactory.createFileResponse(file);
    }

    /**
     * 分页查询文件
     *
     * @param request 查询条件
     * @return 分页结果
     */
    @Operation(summary = "分页查询文件", description = "根据条件分页查询文件列表")
    @GetMapping
    public PageResponse<FileResponse> pageFiles(@Parameter(description = "查询条件") @Valid FileQueryRequest request) {
        Map<String, Object> queryParam = new HashMap<>();
        queryParam.put("storageType", request.getStorageType());
        queryParam.put("used", request.getUsed());

        Page<FileEntity> files = fileService.pageFiles(
                this.getCurrentTenantId(),
                queryParam,
                request.getPage(),
                request.getLimit()
        );

        return responseFactory.createFilePageResponse(files);
    }

    /**
     * 上传文件
     *
     * @param file 文件
     * @return 上传文件响应
     */
    @Operation(summary = "上传文件", description = "上传一个文件")
    @PostMapping
    public FileResponse uploadFile(@RequestParam("file") MultipartFile file) {

        // 检查是否有文件上传
        if (file.isEmpty()) {
            throw new YonchainIllegalStateException("文件不能为空");
        }

        // 检查文件名是否存在
        if (file.getOriginalFilename() == null || file.getOriginalFilename().isEmpty()) {
            throw new YonchainIllegalStateException("文件名不能为空");
        }

        try {
            // 上传文件
            FileEntity uploadFile = fileService.uploadFile(
                    file.getOriginalFilename(),
                    file.getBytes(),
                    file.getContentType(),
                    this.getCurrentUserId(),
                    this.getCurrentTenantId()
            );
            return responseFactory.createFileResponse(uploadFile);
        } catch (IOException e) {
            throw new YonchainException("文件上传失败");
        }
    }

    /**
     * 预览文件
     *
     * @param fileId 文件id
     * @return 上传文件响应
     */
    @Operation(summary = "上传文件", description = "上传一个文件")
    @GetMapping("/{fileId}/file-preview")
    public ResponseEntity<Resource> previewFile(@Parameter(description = "文件ID") @PathVariable String fileId,
                                                @RequestParam String timestamp,
                                                @RequestParam String nonce,
                                                @RequestParam String sign,
                                                @RequestParam(required = false, defaultValue = "false") boolean asAttachment) {
        try {
            // Validate request parameters
            if (timestamp == null || nonce == null || sign == null) {
                throw new YonchainIllegalStateException("无效的请求参数");
            }

            // Verify signature and get file
            if (!fileService.validateSignedUrl(fileId, Long.parseLong(timestamp), nonce, sign)) {
                throw new YonchainIllegalStateException("无效的签名");
            }

            // Get file
            FileEntity uploadFile = fileService.getFileById(fileId);
            if (uploadFile == null) {
                throw new YonchainResourceNotFoundException("文件不存在");
            }

            InputStream inputStream = storageService.downloadFile(uploadFile.getKey());
            byte[] fileContent = inputStream.readAllBytes();

            // Create resource
            ByteArrayResource resource = new ByteArrayResource(fileContent);

            // Build response headers
            HttpHeaders headers = new HttpHeaders();

            // Set content type
            MediaType mediaType = MediaType.parseMediaType(uploadFile.getMimeType());
            headers.setContentType(mediaType);

            // Add Accept-Ranges header for audio/video files
            if (RANGE_SUPPORTED_MIME_TYPES.contains(uploadFile.getMimeType())) {
                headers.set(HttpHeaders.ACCEPT_RANGES, "bytes");
            }

            // Set content length if available
            if (uploadFile.getSize() > 0) {
                headers.setContentLength(uploadFile.getSize());
            }

            // Set as attachment if requested
            if (asAttachment) {
                String encodedFilename = URLEncoder.encode(uploadFile.getName(), StandardCharsets.UTF_8.toString());
                headers.set(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename*=UTF-8''" + encodedFilename);
                headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
            }

            return ResponseEntity.ok()
                    .headers(headers)
                    .body(resource);

        } catch (yonchainException e) {
            throw e;
        } catch (Exception e) {
            throw new YonchainException("预览文件失败", e);
        }
    }

    /**
     * 删除文件
     *
     * @param id 文件ID
     * @return 是否删除成功
     */
    @Operation(summary = "删除文件", description = "根据指定ID删除文件")
    @DeleteMapping("/{id}")
    public ApiResponse<Void> deleteFileById(@Parameter(description = "文件ID") @PathVariable String id) {
        fileService.deleteFileById(id);
        return ApiResponse.success();
    }

    /**
     * 批量删除文件
     *
     * @param ids 文件ID列表
     * @return 是否删除成功
     */
    @Operation(summary = "批量删除文件", description = "根据指定ID批量删除文件")
    @DeleteMapping
    public ApiResponse<Void> deleteFileByIds(@RequestBody List<String> ids) {
        fileService.deleteFileByIds(ids);
        return ApiResponse.success();
    }
}
