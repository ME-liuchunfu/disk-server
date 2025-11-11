package com.spring.boot.disk.server.constant;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum UserStatus {

    OK(0, "正常"),
    DISABLE(1, "封禁"),

    ;

    private int status;

    private String desc;

}
