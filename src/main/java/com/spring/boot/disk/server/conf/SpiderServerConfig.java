package com.spring.boot.disk.server.conf;

import cn.hutool.core.util.StrUtil;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;


@Getter
@Setter
@Configuration
@ConfigurationProperties(prefix = "disk.spider-server")
public class SpiderServerConfig {

    private String url;

    private String showVideo;

    public String joinUri(String uri) {
        if (StrUtil.startWith(uri, "/") || StrUtil.endWith(getUrl(), "/")) {
            return getUrl() + uri;
        }
        return getUrl() + "/" + uri;
    }


}
