package com.spring.boot.disk.server.model.params;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SpiderModel {

    @NotBlank(message = "地址为空")
    private String url;

    @NotNull(message = "目录不能为空")
    private Long folderId;

}
