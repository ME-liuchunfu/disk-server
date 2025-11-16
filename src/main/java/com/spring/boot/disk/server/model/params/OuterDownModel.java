package com.spring.boot.disk.server.model.params;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class OuterDownModel {

    @Size(min = 1, max = 3)
    @NotNull(message = "地址为空")
    private String[] urls;

}
