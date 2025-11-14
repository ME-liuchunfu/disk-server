package com.spring.boot.disk.server.tasks;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum OperateLogType {

    UPLOAD_FILE("10001001", "单文件上传"),
    UPLOAD_FILE_MUl("10001002", "多文件上传"),
    OUTER_FILE_DOWN("10001003", "站外文件下载"),

    ;

    private final String code;

    private final String desc;

}
