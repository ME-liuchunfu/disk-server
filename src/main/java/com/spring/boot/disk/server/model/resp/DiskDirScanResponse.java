package com.spring.boot.disk.server.model.resp;

import lombok.Getter;
import lombok.Setter;
import java.util.Date;

@Getter
@Setter
public class DiskDirScanResponse {

    private Long id;

    private Long parentId;

    private String title;

    private Long refFileId;

    private Integer folder;

    private Date createTime;

    private DiskFileInfo diskFileInfo;

}
