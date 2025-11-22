package com.spring.boot.disk.server.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.spring.boot.disk.server.entity.po.VmUserFile;
import com.spring.boot.disk.server.model.params.*;
import com.spring.boot.disk.server.model.resp.DirSelect;
import com.spring.boot.disk.server.model.resp.DiskDirScanResponse;

import java.util.List;

public interface VmUserFileService extends IService<VmUserFile> {

     List<DiskDirScanResponse> scanner(DiskDirScanModel diskDirScanModel, Long userId);

     void addDiskItem(DiskDirAddModel addModel, Long userId);

     void updateDiskItem(DiskDirUpdateModel updateModel, Long userId);

     void delDiskItem(DiskDirDelModel delModel, Long userId);

    List<DirSelect> getDir(Long parentId, Long userId);

    void updateDiskAvatar(AddDiskAvatarModel avatarModel, Long userId);

}
