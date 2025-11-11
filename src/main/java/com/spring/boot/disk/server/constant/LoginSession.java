package com.spring.boot.disk.server.constant;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum LoginSession {

    LOGIN_USER("LOGIN_USER", "登录信息")

    ;

    private String key;

    private String desc;

}
