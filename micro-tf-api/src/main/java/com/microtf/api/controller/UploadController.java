package com.microtf.api.controller;

import com.microtf.framework.dto.storage.StorageObject;
import com.microtf.framework.services.storage.StorageManagerService;
import com.microtf.framework.services.storage.StorageService;
import io.swagger.annotations.Api;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Optional;

@Api(value = "上传",tags = "upload")
@RestController
@RequestMapping("upload")
public class UploadController {
    private StorageManagerService storageManagerService;

    @Autowired
    public void setStorageManagerService(StorageManagerService storageManagerService) {
        this.storageManagerService = storageManagerService;
    }

    @PostMapping("/upload")
    public String  upload(MultipartFile file, @RequestParam String fileName,@RequestParam String store) {
        Optional<StorageService> storageService = storageManagerService.selectStorage(store);
        if(storageService.isPresent()){
            try {
                StorageObject upload = storageService.get().upload(file.getInputStream(), store + "/" + fileName);
                return upload.getUrl();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return "不存在";
        }else {
            return "store 不存在";
        }
    }
}
