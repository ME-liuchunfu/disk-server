package com.spring.boot.disk.server.model.params;

import com.spring.boot.disk.server.constant.DiskIItemType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DiskDirAddModel {

    private Long parentId;

    @NotBlank(message = "目录名不能为空")
    private String title;

    private Long refFileId;

    private String outer;

    @NotNull(message = "类型不能为空")
    private Integer folder = DiskIItemType.FOLDER.getCode();

    @NotNull(message = "来源为空")
    private Integer from = DiskIItemType.INNER.getCode();

}
