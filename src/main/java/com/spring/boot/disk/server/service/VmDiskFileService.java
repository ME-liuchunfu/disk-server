package com.spring.boot.disk.server.service;


import com.baomidou.mybatisplus.extension.service.IService;
import com.spring.boot.disk.server.entity.po.VmDiskFile;
import com.spring.boot.disk.server.model.FileRes;
import com.spring.boot.disk.server.model.params.OuterDownModel;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface VmDiskFileService extends IService<VmDiskFile> {

    void insert(VmDiskFile vmDiskFile);

    long countUnKey(VmDiskFile vmDiskFile);

    VmDiskFile selectByHashValue(String hashValue);

    FileRes uploadFile(MultipartFile file, String userName);

    List<FileRes> uploadFiles(MultipartFile[] files, String userName);

    List<VmDiskFile> selectListByIds(List<Long> fileIdList);

    List<FileRes> outerDown(OuterDownModel outerModel, String userName);

    String token();

}
