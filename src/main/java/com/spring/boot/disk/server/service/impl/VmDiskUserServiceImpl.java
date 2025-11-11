package com.spring.boot.disk.server.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.conditions.query.LambdaQueryChainWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.spring.boot.disk.server.entity.po.VmDiskUser;
import com.spring.boot.disk.server.mapper.VmDiskUserMapper;
import com.spring.boot.disk.server.service.VmDiskUserService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;


@Slf4j
@Service
public class VmDiskUserServiceImpl extends ServiceImpl<VmDiskUserMapper, VmDiskUser> implements VmDiskUserService {

    @Resource
    VmDiskUserMapper vmDiskUserMapper;

    @Override
    public VmDiskUser selectByUserName(String userName) {
        LambdaQueryWrapper<VmDiskUser> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(VmDiskUser::getUserName, userName);
        return this.getOne(queryWrapper, false);
    }

}
