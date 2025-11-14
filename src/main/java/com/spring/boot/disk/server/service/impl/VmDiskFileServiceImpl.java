package com.spring.boot.disk.server.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.lang.UUID;
import cn.hutool.http.HttpUtil;
import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.spring.boot.disk.server.conf.DiskServerConfig;
import com.spring.boot.disk.server.conf.SpiderServerConfig;
import com.spring.boot.disk.server.entity.po.VmDiskFile;
import com.spring.boot.disk.server.exception.AppException;
import com.spring.boot.disk.server.mapper.VmDiskFileMapper;
import com.spring.boot.disk.server.model.FileRes;
import com.spring.boot.disk.server.model.params.OuterDownModel;
import com.spring.boot.disk.server.security.SecurityContext;
import com.spring.boot.disk.server.service.VmDiskFileService;
import com.spring.boot.disk.server.tasks.OperateLogTask;
import com.spring.boot.disk.server.tasks.OperateLogType;
import com.spring.boot.http.down.CustomHeaderDownloadManager;
import com.spring.boot.http.down.DownloadTaskConfig;
import com.spring.boot.http.down.ThreadPoolConfig;
import com.spring.boot.utils.CountStr;
import com.spring.boot.utils.file.FileHasher;
import com.spring.boot.utils.file.FileInfo;
import com.spring.boot.utils.file.FileInfoUtil;
import jakarta.annotation.Resource;
import jakarta.validation.constraints.NotBlank;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.net.URLEncoder;
import java.nio.file.Path;
import java.util.*;
import java.util.concurrent.Callable;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


@Slf4j
@Service
public class VmDiskFileServiceImpl extends ServiceImpl<VmDiskFileMapper, VmDiskFile> implements VmDiskFileService {

    @Resource
    VmDiskFileMapper vmDiskFileMapper;

    @Resource
    DiskServerConfig diskServerConfig;

    @Resource
    SpiderServerConfig spiderServerConfig;

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
        if (CollectionUtil.isEmpty(fileIdList)) {
            return new ArrayList<>();
        }
        LambdaQueryWrapper<VmDiskFile> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.in(VmDiskFile::getId, fileIdList);
        return this.list(queryWrapper);
    }

    public static String extractUrl(String url) {
        // 与Python正则表达式等价的Java正则表达式
        String regex = "http[s]?:\\/\\/[\\w.-]+[\\w\\/-]*[\\w.-]*\\??[\\w=&:\\-\\+%]*[/]*";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(url);

        if (matcher.find()) {
            return matcher.group();
        }
        return null;
    }

    @Override
    public List<FileRes> outerDown(OuterDownModel outerModel, String userName) {
        String serverApi = spiderServerConfig.joinUri(spiderServerConfig.getShowVideo());
        String[] urls = outerModel.getUrls();
        CompletableFuture<FileRes>[] futures = new CompletableFuture[urls.length];
        for (int i = 0; i < urls.length; i++) {
            String url = urls[i];
            futures[i] = CompletableFuture.supplyAsync(()->{
                FileRes fileRes = null;
                try {
                    String down = extractUrl(url);
                    String encode = URLEncoder.encode(down, "utf-8");
                    String response = HttpUtil.get(serverApi + encode);
                    JSONObject jsonObject = JSON.parseObject(response);
                    JSONObject data = jsonObject.getJSONObject("data");
                    String video_url = data.getString("video_url");
                    String cover_url = data.getString("cover_url");
                    String title = data.getString("title");
                    File mp4tempFile = File.createTempFile(UUID.fastUUID().toString(), ".mp4");
                    HttpUtil.downloadFile(video_url, mp4tempFile);
                    String dirPath = diskServerConfig.dirPath();
                    Path path = diskServerConfig.fromDirPath(dirPath);
                    String hashValue = FileHasher.calculateFileHash(mp4tempFile, FileHasher.Algorithm.SHA256);
                    FileInfo fileInfo = FileInfoUtil.transferTo(path, mp4tempFile, title, hashValue);
                    fileRes = new FileRes();
                    fileRes.setFileSize(fileInfo.getFileSize());
                    fileRes.setOriginalFilename(fileInfo.getFileName());
                    fileRes.setPath(fileInfo.getRelativePath());
                    fileRes.setHashValue(fileInfo.getHashValue());

                    // 写入
                    VmDiskFile vmDiskFile = new VmDiskFile();
                    vmDiskFile.setFileSize(fileInfo.getFileSize());
                    vmDiskFile.setFileName(fileInfo.getFileName());
                    vmDiskFile.setPath(fileInfo.getRelativePath());
                    vmDiskFile.setHashValue(fileInfo.getHashValue());
                    vmDiskFile.setFileType(fileInfo.getFileType());
                    vmDiskFile.setOwner(userName);
                    this.insert(vmDiskFile);
                    fileRes.setRefId(vmDiskFile.getId());
                    OperateLogTask.getIns().logOperate(CountStr.concat(this.getClass(), "outerDown"), jsonObject.toJSONString(), OperateLogType.OUTER_FILE_DOWN, userName);

//                    File imgtempFile = File.createTempFile(UUID.fastUUID().toString(), ".png");
//                    HttpUtil.downloadFile(cover_url, imgtempFile);
                    if (mp4tempFile.exists()) {
                        mp4tempFile.delete();
                    }
                } catch (Exception e) {
                    log.error("解析错误, url:{}", url, e);
                }
                return fileRes;
            });
        }
        List<FileRes> resList = new ArrayList<>();
        try {
            CompletableFuture.allOf(futures).get();
            for (CompletableFuture<FileRes> future : futures) {
                FileRes fileRes = future.get();
                if (Objects.nonNull(fileRes)) {
                    resList.add(fileRes);
                }
            }
        } catch (Exception e) {
            log.error("下载错误", e);
        }
        return resList;
    }

}
