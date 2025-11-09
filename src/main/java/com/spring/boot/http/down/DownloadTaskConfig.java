package com.spring.boot.http.down;


import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 下载任务配置类（包含请求头设置）
 */
@Getter
@Setter
@ToString
public class DownloadTaskConfig {

    private final String url;
    private final Path savePath;
    private final Map<String, List<String>> headers = new HashMap<>();

    public DownloadTaskConfig(String url, Path savePath) {
        this.url = url;
        this.savePath = savePath;
    }

    /**
     * 添加请求头（单值）
     */
    public DownloadTaskConfig addHeader(String name, String value) {
        headers.computeIfAbsent(name, k -> new ArrayList<>()).add(value);
        return this;
    }

    /**
     * 添加请求头（多值）
     */
    public DownloadTaskConfig addHeaders(String name, List<String> values) {
        headers.computeIfAbsent(name, k -> new ArrayList<>()).addAll(values);
        return this;
    }

    /**
     * 设置请求头（覆盖现有值）
     */
    public DownloadTaskConfig setHeader(String name, String value) {
        List<String> values = new ArrayList<>();
        values.add(value);
        headers.put(name, values);
        return this;
    }

}