package com.spring.boot.disk.server.security;

import cn.dev33.satoken.stp.StpInterface;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;

@Slf4j
@Component
public class StpInterfaceImpl implements StpInterface {

    @Override
    public List<String> getPermissionList(Object loginId, String loginType) {
        log.info("getPermissionList: loginId:{}, loginType:{}", loginId, loginType);
        return Collections.emptyList();
    }

    @Override
    public List<String> getRoleList(Object loginId, String loginType) {
        log.info("getRoleList: loginId:{}, loginType:{}", loginId, loginType);
        return Collections.emptyList();
    }

}
