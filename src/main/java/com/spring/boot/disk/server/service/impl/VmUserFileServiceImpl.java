package com.spring.boot.disk.server.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.spring.boot.disk.server.entity.po.VmUserFile;
import com.spring.boot.disk.server.mapper.VmUserFileMapper;
import com.spring.boot.disk.server.service.VmUserFileService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class VmUserFileServiceImpl extends ServiceImpl<VmUserFileMapper, VmUserFile> implements VmUserFileService {

    @Resource
    VmUserFileMapper vmUserFileMapper;

}
