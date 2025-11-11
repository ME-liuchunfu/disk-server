package com.spring.boot.disk.server.service;


import com.baomidou.mybatisplus.extension.service.IService;
import com.spring.boot.disk.server.entity.po.VmDiskUser;

public interface VmDiskUserService extends IService<VmDiskUser> {

    VmDiskUser selectByUserName(String userName);

}
