package com.spring.boot.disk.server.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.spring.boot.disk.server.constant.DiskIItemType;
import com.spring.boot.disk.server.entity.po.VmDiskFile;
import com.spring.boot.disk.server.entity.po.VmUserFile;
import com.spring.boot.disk.server.exception.AppException;
import com.spring.boot.disk.server.mapper.VmUserFileMapper;
import com.spring.boot.disk.server.mapstruct.ConvertFactory;
import com.spring.boot.disk.server.model.params.*;
import com.spring.boot.disk.server.model.resp.DirSelect;
import com.spring.boot.disk.server.model.resp.DiskDirScanResponse;
import com.spring.boot.disk.server.model.resp.DiskFileInfo;
import com.spring.boot.disk.server.service.VmDiskFileService;
import com.spring.boot.disk.server.service.VmUserFileService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Slf4j
@Service
public class VmUserFileServiceImpl extends ServiceImpl<VmUserFileMapper, VmUserFile> implements VmUserFileService {

    @Resource
    VmUserFileMapper vmUserFileMapper;

    @Resource
    VmDiskFileService vmDiskFileService;

    @Override
    public List<DiskDirScanResponse> scanner(DiskDirScanModel diskDirScanModel, Long userId) {
        LambdaQueryWrapper<VmUserFile> queryWrapper = new LambdaQueryWrapper<>();
        Long parentId = diskDirScanModel.getParentId();
        if (Objects.nonNull(parentId)) {
            queryWrapper.eq(VmUserFile::getParentId, parentId);
        } else {
            queryWrapper.isNull(VmUserFile::getParentId);
        }
        queryWrapper.eq(VmUserFile::getOwner, userId);
        Integer folder = diskDirScanModel.getFolder();
        if (Objects.nonNull(folder)) {
            queryWrapper.eq(VmUserFile::getFolder, folder);
        }
        String title = diskDirScanModel.getTitle();
        if (StrUtil.isNotBlank(title)) {
            queryWrapper.like(VmUserFile::getTitle, title);
        }

        queryWrapper.orderByAsc(VmUserFile::getId);
        List<VmUserFile> list = this.list(queryWrapper);
        List<DiskDirScanResponse> responseList = list.stream().map(ConvertFactory.INST::toDiskDirScanResponse).collect(Collectors.toList());
        if (CollectionUtil.isNotEmpty(responseList)) {
            // 注入文件
            List<Long> fileIdList = responseList.stream()
                    .filter(c -> c.getFolder() == DiskIItemType.FILE.getCode())
                    .map(DiskDirScanResponse::getRefFileId)
                    .distinct()
                    .collect(Collectors.toList());

            fileIdList.addAll(responseList.stream().map(DiskDirScanResponse::getRefAvatarFileId).filter(Objects::nonNull).distinct().toList());
            List<VmDiskFile> diskFileList = vmDiskFileService.selectListByIds(new ArrayList<>(new HashSet<>(fileIdList)));

            Map<Long, DiskFileInfo> fileMap = diskFileList.stream()
                    .map(ConvertFactory.INST::toDiskFileInfo)
                    .collect(Collectors.toMap(DiskFileInfo::getId, Function.identity(), (l, r) -> l));

            for (DiskDirScanResponse response : responseList) {
                response.setDiskFileInfo(fileMap.get(response.getRefFileId()));
                DiskFileInfo fileInfo = fileMap.get(response.getRefAvatarFileId());
                if (Objects.nonNull(fileInfo)) {
                    response.setAvatarPath(fileInfo.getPath());
                }
            }
        }
        return responseList;
    }

    @Override
    public void addDiskItem(DiskDirAddModel addModel, Long userId) {
        // 先查询是否有同名目录
        LambdaQueryWrapper<VmUserFile> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(VmUserFile::getOwner, userId);
        queryWrapper.eq(VmUserFile::getTitle, addModel.getTitle());
        Long parentId = addModel.getParentId();
        if (Objects.nonNull(parentId)) {
            queryWrapper.eq(VmUserFile::getParentId, parentId);
        } else {
            queryWrapper.isNull(VmUserFile::getParentId);
        }
        long count = this.count(queryWrapper);
        if (count > 0) {
            throw new AppException("文件或目录已存在:" + addModel.getTitle());
        }
        VmUserFile vmUserFile = new VmUserFile();
        if (Objects.nonNull(parentId)) {
            vmUserFile.setParentId(parentId);
        }
        if (DiskIItemType.FILE.getCode() == addModel.getFolder() && Objects.isNull(addModel.getRefFileId())) {
            throw new AppException("缺失文件");
        }
        vmUserFile.setTitle(addModel.getTitle());
        vmUserFile.setRefFileId(addModel.getRefFileId());
        vmUserFile.setFolder(addModel.getFolder());
        vmUserFile.setCreateTime(new Date());
        vmUserFile.setOwner(userId);
        this.save(vmUserFile);
        // 这里要开启一个线程处理文件下载
    }

    @Override
    public void updateDiskItem(DiskDirUpdateModel updateModel, Long userId) {
        // 查询文件是否存在，并且文件名是否存在
        LambdaQueryWrapper<VmUserFile> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(VmUserFile::getOwner, userId);
        queryWrapper.eq(VmUserFile::getId, updateModel.getId());
        long count = this.count(queryWrapper);
        if (count == 0) {
            throw new AppException("目录或文件不存在,不可修改");
        }
        queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(VmUserFile::getOwner, userId);
        Long parentId = updateModel.getParentId();
        if (Objects.nonNull(parentId)) {
            queryWrapper.eq(VmUserFile::getParentId, parentId);
        } else {
            queryWrapper.isNull(VmUserFile::getParentId);
        }
        queryWrapper.eq(VmUserFile::getTitle, updateModel.getTitle());
        long updateCount = this.count(queryWrapper);
        if (updateCount > 0) {
            throw new AppException("目录或文件存在,不可修改");
        }
        LambdaUpdateWrapper<VmUserFile> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.eq(VmUserFile::getId, updateModel.getId())
                .eq(VmUserFile::getOwner, userId);
        VmUserFile vmUserFile = new VmUserFile();
        vmUserFile.setParentId(updateModel.getParentId());
        vmUserFile.setTitle(updateModel.getTitle());
        this.update(vmUserFile, updateWrapper);
    }

    @Override
    public void delDiskItem(DiskDirDelModel delModel, Long userId) {
        HashSet<Long> delIdsSet = new HashSet<>();
        List<Long> queryIds = new ArrayList<>(Arrays.asList(delModel.getIds()));
        LambdaQueryWrapper<VmUserFile> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(VmUserFile::getOwner, userId)
                .in(VmUserFile::getId, queryIds);
        List<VmUserFile> fileList = this.list(queryWrapper);
        delIdsSet.addAll(fileList.stream().map(VmUserFile::getId).toList());
        queryIds = fileList.stream()
                .filter(v -> v.getFolder() == DiskIItemType.FOLDER.getCode())
                .map(VmUserFile::getId)
                .distinct()
                .toList();
        boolean next = !queryIds.isEmpty();
        while (next) {
            queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(VmUserFile::getOwner, userId)
                    .in(VmUserFile::getParentId, queryIds);
            fileList = this.list(queryWrapper);

            delIdsSet.addAll(fileList.stream().map(VmUserFile::getId).toList());

            queryIds = fileList.stream()
                    .filter(v -> v.getFolder() == DiskIItemType.FOLDER.getCode())
                    .map(VmUserFile::getId)
                    .distinct()
                    .toList();
            if (queryIds.isEmpty()) {
                next = false;
            }
        }
        // 删除
        LambdaQueryWrapper<VmUserFile> delWrapper = new LambdaQueryWrapper<>();
        delWrapper.in(VmUserFile::getId, delIdsSet);
        this.remove(delWrapper);
    }

    @Override
    public List<DirSelect> getDir(Long parentId, Long userId) {
        LambdaQueryWrapper<VmUserFile> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(VmUserFile::getOwner, userId)
                .eq(VmUserFile::getFolder, DiskIItemType.FOLDER.getCode());
        if (Objects.isNull(parentId) || parentId == 0) {
            queryWrapper.isNull(VmUserFile::getParentId);
        } else {
            queryWrapper.eq(VmUserFile::getParentId, parentId);
        }
        List<VmUserFile> list = this.list(queryWrapper);
        ArrayList<DirSelect> dirSelects = new ArrayList<>();
        for (VmUserFile userFile : list) {
            DirSelect dirSelect = new DirSelect();
            dirSelect.setId(userFile.getId());
            dirSelect.setName(userFile.getTitle());
            dirSelects.add(dirSelect);
        }
        return dirSelects;
    }

    @Override
    public void updateDiskAvatar(AddDiskAvatarModel avatarModel, Long userId) {
        LambdaUpdateWrapper<VmUserFile> queryWrapper = new LambdaUpdateWrapper<>();
        queryWrapper.eq(VmUserFile::getOwner, userId);
        queryWrapper.eq(VmUserFile::getId, avatarModel.getId());
        long count = this.count(queryWrapper);
        if (count == 0) {
            throw new AppException("当前资源无权修改");
        }
        LambdaUpdateWrapper<VmUserFile> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.eq(VmUserFile::getOwner, userId);
        updateWrapper.eq(VmUserFile::getId, avatarModel.getId());
        VmUserFile vmUserFile = new VmUserFile();
        vmUserFile.setRefAvatarFileId(avatarModel.getFileId());
        this.update(vmUserFile, updateWrapper);
    }

}
