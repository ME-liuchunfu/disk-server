package com.spring.boot.disk.server.constant;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum LoginDriverType {

    PC("PC", "PC端")

    ;

    private String driver;

    private String desc;

}
