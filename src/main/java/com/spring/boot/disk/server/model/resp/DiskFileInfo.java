package com.spring.boot.disk.server.model.resp;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DiskFileInfo {

    private Long fileId;

    private String fileName;

    private Long fileSize;

    private String fileType;

    private String path;

    private String prefViewPath;

}
