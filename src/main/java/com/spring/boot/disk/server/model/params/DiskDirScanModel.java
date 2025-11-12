package com.spring.boot.disk.server.model.params;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DiskDirScanModel {

    private Long parentId;

    private String title;

    private Integer folder;

}
