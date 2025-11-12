package com.spring.boot.disk.server.constant;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum DiskIItemType {

    FOLDER(1, "目录"),
    FILE(0, "文件"),

    ;

    private int code;

    private String desc;

}

