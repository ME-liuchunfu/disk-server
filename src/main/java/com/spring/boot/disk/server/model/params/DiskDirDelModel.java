package com.spring.boot.disk.server.model.params;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DiskDirDelModel {

    @Size(min = 0, max = 100)
    @NotNull(message = "文件为空")
    private Long[] ids;

}
