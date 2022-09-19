package com.microtf.api.controller;

import com.microtf.framework.dto.BaseResponse;
import com.microtf.framework.dto.Response;
import com.microtf.framework.dto.common.EditorUpload;
import com.microtf.framework.dto.storage.StorageObject;
import com.microtf.framework.exceptions.BizException;
import com.microtf.framework.services.storage.StorageManagerService;
import com.microtf.framework.services.storage.StorageService;
import com.microtf.framework.utils.ResponseUtil;
import io.swagger.annotations.Api;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Optional;
import java.util.UUID;

/**
 * 上传控制器
 * @author guliuzhong
 */
@Api(value = "上传", tags = "upload")
@RestController
@RequestMapping("upload")
@Slf4j
public class UploadController {
    private StorageManagerService storageManagerService;

    @Autowired
    public void setStorageManagerService(StorageManagerService storageManagerService) {
        this.storageManagerService = storageManagerService;
    }

    @PostMapping("/editor")
    public Response<EditorUpload> editor(MultipartFile file) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        String store = "sentany";
        EditorUpload.EditorUploadBuilder builder = EditorUpload.builder();
        Optional<StorageService> storageService = storageManagerService.selectStorage(store);
        StringBuilder fileNameBuffer = new StringBuilder();
        fileNameBuffer.append(store).append("/");
        fileNameBuffer.append(simpleDateFormat.format(new Date())).append("/").append(UUID.randomUUID());
        if (storageService.isPresent()) {
            try {
                StorageObject upload = storageService.get().upload(file.getInputStream(), fileNameBuffer.toString(), file.getContentType());
                builder.url(upload.getUrl());
                builder.alt(file.getOriginalFilename());
                builder.href(upload.getUrl());
                return ResponseUtil.responseData(builder.build());
            } catch (IOException e) {
                log.error("上传出错");
                throw new BizException("上传出错" + e.getMessage(), BaseResponse.ErrorShowType.ERROR_MESSAGE);
            }
        } else {
            throw new BizException("上传出错", BaseResponse.ErrorShowType.ERROR_MESSAGE);
        }
    }
}
