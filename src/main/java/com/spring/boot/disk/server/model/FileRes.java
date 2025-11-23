package com.spring.boot.disk.server.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class FileRes {

    private Long refId;

    private String originalFilename;

    private String path;

    private long fileSize;

    private String hashValue;

    private String coverPath;

    private Long coverId;

}
