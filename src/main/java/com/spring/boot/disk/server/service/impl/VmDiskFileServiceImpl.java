package com.spring.boot.disk.server.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.spring.boot.disk.server.conf.DiskServerConfig;
import com.spring.boot.disk.server.entity.po.VmDiskFile;
import com.spring.boot.disk.server.exception.AppException;
import com.spring.boot.disk.server.mapper.VmDiskFileMapper;
import com.spring.boot.disk.server.model.FileRes;
import com.spring.boot.disk.server.security.SecurityContext;
import com.spring.boot.disk.server.service.VmDiskFileService;
import com.spring.boot.disk.server.tasks.OperateLogTask;
import com.spring.boot.disk.server.tasks.OperateLogType;
import com.spring.boot.utils.CountStr;
import com.spring.boot.utils.file.FileHasher;
import com.spring.boot.utils.file.FileInfo;
import com.spring.boot.utils.file.FileInfoUtil;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;


@Slf4j
@Service
public class VmDiskFileServiceImpl extends ServiceImpl<VmDiskFileMapper, VmDiskFile> implements VmDiskFileService {

    @Resource
    VmDiskFileMapper vmDiskFileMapper;

    @Resource
    DiskServerConfig diskServerConfig;

    @Override
    public void insert(VmDiskFile vmDiskFile) {
        vmDiskFile.setStatDate(new java.sql.Date(System.currentTimeMillis()));
        vmDiskFile.setCreateTime(new Date());
        this.save(vmDiskFile);
    }

    @Override
    public long countUnKey(VmDiskFile vmDiskFile) {
        LambdaQueryWrapper<VmDiskFile> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(VmDiskFile::getHashValue, vmDiskFile.getHashValue());
        return this.count(queryWrapper);
    }

    @Override
    public VmDiskFile selectByHashValue(String hashValue) {
        LambdaQueryWrapper<VmDiskFile> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(VmDiskFile::getHashValue, hashValue);
        return baseMapper.selectOne(queryWrapper);
    }

    @Override
    public FileRes uploadFile(MultipartFile file, String userName) {
        if (file.isEmpty()) {
            throw new AppException("文件为空");
        }
        FileRes fileRes = new FileRes();
        try {
            String dirPath = diskServerConfig.dirPath();
            Path path = diskServerConfig.fromDirPath(dirPath);
            String hashValue = FileHasher.calculateFileHash(file, FileHasher.Algorithm.SHA256);
            // 从库中查
            VmDiskFile vmDiskFile = this.selectByHashValue(hashValue);
            if (Objects.nonNull(vmDiskFile)) {
                fileRes.setFileSize(vmDiskFile.getFileSize());
                fileRes.setOriginalFilename(vmDiskFile.getFileName());
                fileRes.setPath(vmDiskFile.getPath());
                fileRes.setHashValue(vmDiskFile.getHashValue());
                fileRes.setRefId(vmDiskFile.getId());
            } else {
                FileInfo fileInfo = FileInfoUtil.transferTo(path, file, file.getOriginalFilename(), hashValue);
                fileRes.setFileSize(fileInfo.getFileSize());
                fileRes.setOriginalFilename(fileInfo.getFileName());
                fileRes.setPath(fileInfo.getRelativePath());
                fileRes.setHashValue(fileInfo.getHashValue());

                // 写入
                vmDiskFile = new VmDiskFile();
                vmDiskFile.setFileSize(fileInfo.getFileSize());
                vmDiskFile.setFileName(fileInfo.getFileName());
                vmDiskFile.setPath(fileInfo.getRelativePath());
                vmDiskFile.setHashValue(fileInfo.getHashValue());
                vmDiskFile.setFileType(fileInfo.getFileType());
                vmDiskFile.setOwner(userName);
                this.insert(vmDiskFile);
                fileRes.setRefId(vmDiskFile.getId());
                OperateLogTask.getIns().logOperate(CountStr.concat(this.getClass(), "uploadFile"), null, OperateLogType.UPLOAD_FILE, userName);
            }
        } catch (Exception e) {
            throw new AppException("文件上传错误", e);
        }
        return fileRes;
    }

    @Override
    public List<FileRes> uploadFiles(MultipartFile[] files, String userName) {
        if (files.length == 0) {
            throw new AppException("文件为空");
        }
        for (MultipartFile file : files) {
            if (file.isEmpty()) {
                throw new AppException("请勿上传空文件");
            }
        }
        OperateLogTask.getIns().logOperate(CountStr.concat(this.getClass(), "uploadFiles"), null, OperateLogType.UPLOAD_FILE_MUl, userName);
        ArrayList<FileRes> fileRes = new ArrayList<>();
        for (MultipartFile file : files) {
            try {
                fileRes.add(this.uploadFile(file, userName));
            } catch (Exception e) {
                log.error("文件上传错误，filename:{}", file.getOriginalFilename(), e);
            }
        }
        return fileRes;
    }

    @Override
    public List<VmDiskFile> selectListByIds(List<Long> fileIdList) {
        LambdaQueryWrapper<VmDiskFile> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.in(VmDiskFile::getId, fileIdList);
        return this.list(queryWrapper);
    }

}
