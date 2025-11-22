package com.spring.boot.disk.server.model.params;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AddDiskAvatarModel {

    @NotNull(message = "id为空")
    private Long id;

    @NotNull(message = "资源为空")
    private Long fileId;

}
