package com.spring.boot.disk.server.constant;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum DiskIItemType {

    FOLDER(1, "目录"),
    FILE(0, "文件"),

    INNER(1, "站内"),
    OUTER(0, "站外"),

    ;

    private int code;

    private String desc;

}

