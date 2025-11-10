package com.spring.boot.utils;

import com.spring.boot.utils.file.FileInfo;
import com.spring.boot.utils.file.FileInfoUtil;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class FileTest {

    @Test
    public void run() throws IOException {
        String dir = "E:\\temp_svn_doc";
        List<FileInfo> allFileInfos = FileInfoUtil.getAllFileInfos(dir);
        Map<String, List<FileInfo>> collect = allFileInfos.stream().collect(Collectors.groupingBy(FileInfo::getHashValue));
        System.out.println(collect);
    }

}
