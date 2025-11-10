package com.spring.boot.disk.server.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.spring.boot.disk.server.entity.po.VmOperateLog;
import com.spring.boot.disk.server.mapper.VmOperateLogMapper;
import com.spring.boot.disk.server.service.VmOperateLogService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class VmOperateLogServiceImpl extends ServiceImpl<VmOperateLogMapper, VmOperateLog> implements VmOperateLogService {

    @Resource
    VmOperateLogMapper vmOperateLogMapper;

}
