package com.spring.boot.disk.server.tasks;

import com.alibaba.fastjson2.JSON;
import com.spring.boot.disk.server.conf.ThreadConfig;
import com.spring.boot.disk.server.entity.po.VmOperateLog;
import com.spring.boot.disk.server.service.VmOperateLogService;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.Resource;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.Objects;

@Component
public class OperateLogTask {

    @Resource
    VmOperateLogService vmOperateLogService;

    private static OperateLogTask INS = null;

    public static OperateLogTask getIns() {
        return INS;
    }

    @PostConstruct
    private void init() {
        INS = this;
    }

    @Async(ThreadConfig.THREAD_POOL_NAME)
    public void logOperate(String method, Object params, OperateLogType type, String owner) {
        VmOperateLog vmOperateLog = new VmOperateLog();
        vmOperateLog.setInvokeMethod(method);
        vmOperateLog.setInvokeParams(tojson(params));
        vmOperateLog.setInvokeType(type.getCode());
        vmOperateLog.setCreateTime(new Date());
        vmOperateLog.setOwner(owner);
        vmOperateLogService.save(vmOperateLog);
    }

    private String tojson(Object params) {
        try {
            if (Objects.isNull(params)) {
                return "null";
            }
            if (params instanceof String) {
                return (String) params;
            }
            return JSON.toJSONString(params);
        } catch (Exception e) {
            return Objects.toString(params);
        }
    }

}
