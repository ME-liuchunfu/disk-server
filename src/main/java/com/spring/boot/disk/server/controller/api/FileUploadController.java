package com.spring.boot.disk.server.controller.api;

import com.alibaba.fastjson2.JSON;
import com.spring.boot.disk.server.model.FileRes;
import com.spring.boot.disk.server.model.Rs;
import com.spring.boot.disk.server.model.params.OuterDownModel;
import com.spring.boot.disk.server.security.SecurityContext;
import com.spring.boot.disk.server.service.VmDiskFileService;
import jakarta.annotation.Resource;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import java.util.List;

@RequestMapping("api/disk/file/manager")
@RestController
public class FileUploadController {

    @Resource
    VmDiskFileService vmDiskFileService;

    @PostMapping("/upload")
    public Rs uploadFile(@RequestParam("file") MultipartFile file) {
        FileRes fileRes = vmDiskFileService.uploadFile(file, SecurityContext.getUserName());
        return Rs.ok().data(fileRes);
    }

    @PostMapping("/uploadMultiple")
    public Rs uploadMultipleFiles(@RequestParam("files") MultipartFile[] files) {
        List<FileRes> fileRes = vmDiskFileService.uploadFiles(files, SecurityContext.getUserName());
        return Rs.ok().data(fileRes);
    }

    @PostMapping("/outerDown")
    public Rs outerDown(@Validated @RequestBody OuterDownModel outerModel) {
        List<FileRes> fileRes = vmDiskFileService.outerDown(outerModel, SecurityContext.getUserName());
        return Rs.ok().data(fileRes);
    }

}
