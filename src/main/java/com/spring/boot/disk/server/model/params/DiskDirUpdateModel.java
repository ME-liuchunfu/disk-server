package com.spring.boot.disk.server.model.params;


import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DiskDirUpdateModel {

    @NotNull(message = "文件为空")
    private Long id;

    private Long parentId;

    @NotBlank(message = "目录名为空")
    private String title;

}
