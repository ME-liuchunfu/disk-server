package com.spring.boot.disk.server.controller.api;


import com.spring.boot.disk.server.model.Rs;
import com.spring.boot.disk.server.model.params.*;
import com.spring.boot.disk.server.security.SecurityContext;
import com.spring.boot.disk.server.service.VmUserFileService;
import jakarta.annotation.Resource;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RequestMapping("api/disk/dir/ref/manager")
@RestController
public class ApiDiskDirRefController {

    @Resource
    VmUserFileService vmUserFileService;

    @PostMapping("/scan")
    public Rs scanner(@Validated @RequestBody DiskDirScanModel diskDirScanModel) {
        Long userId = SecurityContext.getUserId();
        return Rs.ok().data(vmUserFileService.scanner(diskDirScanModel, userId));
    }

    @PostMapping("/getDir/{parentId}")
    public Rs getDir(@PathVariable Long parentId) {
        Long userId = SecurityContext.getUserId();
        return Rs.ok().data(vmUserFileService.getDir(parentId, userId));
    }

    @PostMapping("/add")
    public Rs add(@Validated @RequestBody DiskDirAddModel addModel) {
        Long userId = SecurityContext.getUserId();
        vmUserFileService.addDiskItem(addModel, userId);
        return Rs.ok();
    }

    @PostMapping("/update")
    public Rs update(@Validated @RequestBody DiskDirUpdateModel updateModel) {
        Long userId = SecurityContext.getUserId();
        vmUserFileService.updateDiskItem(updateModel, userId);
        return Rs.ok();
    }

    @PostMapping("/del")
    public Rs del(@Validated @RequestBody DiskDirDelModel delModel) {
        Long userId = SecurityContext.getUserId();
        vmUserFileService.delDiskItem(delModel, userId);
        return Rs.ok();
    }

    @PostMapping("/update/avatar")
    public Rs updateAvatar(@Validated @RequestBody AddDiskAvatarModel avatarModel) {
        Long userId = SecurityContext.getUserId();
        vmUserFileService.updateDiskAvatar(avatarModel, userId);
        return Rs.ok();
    }
}
