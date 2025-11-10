package com.spring.boot.disk.server.controller.api;

import com.spring.boot.disk.server.model.FileRes;
import com.spring.boot.disk.server.model.Rs;
import com.spring.boot.disk.server.security.SecurityContext;
import com.spring.boot.disk.server.service.VmDiskFileService;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
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

}
