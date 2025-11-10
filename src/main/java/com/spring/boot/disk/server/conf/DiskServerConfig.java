package com.spring.boot.disk.server.conf;

import cn.hutool.core.util.StrUtil;
import com.spring.boot.disk.server.exception.AppException;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Getter
@Setter
@Configuration
@ConfigurationProperties(prefix = "disk.server")
public class DiskServerConfig {

    private String savePath;

    private boolean formEnv;

    public String dirPath() {
        if (isFormEnv()) {
            String envPath = System.getenv("savePath");
            if (StrUtil.isNotBlank(envPath)) {
                return envPath;
            }
        }
        return getSavePath();
    }

    public Path fromDirPath(String path) {
        Path dir = Paths.get(path);
        if (Files.notExists(dir)) {
            try {
                Files.createDirectories(dir);
            } catch (Exception e) {
                throw new AppException(e.getMessage(), 500, e);
            }
        }
        return dir;
    }

}
