package com.spring.boot.utils.file;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

/**
 * 文件信息Java Bean，存储文件的相关属性
 */
@Getter
@Setter
public class FileInfo implements Serializable {

    private static final long serialVersionUID = 1L;

    // 文件名
    private String fileName;
    // 文件在指定目录下的相对路径
    private String relativePath;
    // 文件类型（扩展名）
    private String fileType;
    // 文件大小（字节）
    private long fileSize;
    // 文件的SHA-256哈希值
    private String hashValue;

    // 无参构造函数
    public FileInfo() {}

    // 全参构造函数
    public FileInfo(String fileName, String relativePath, String fileType, long fileSize, String hashValue) {
        this.fileName = fileName;
        this.relativePath = relativePath;
        this.fileType = fileType;
        this.fileSize = fileSize;
        this.hashValue = hashValue;
    }

    @Override
    public String toString() {
        return "FileInfo{" +
                "fileName='" + fileName + '\'' +
                ", relativePath='" + relativePath + '\'' +
                ", fileType='" + fileType + '\'' +
                ", fileSize=" + fileSize +
                ", hashValue='" + hashValue + '\'' +
                '}';
    }

}
