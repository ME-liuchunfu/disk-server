package com.spring.boot.disk.server.model.params;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AuthModel {

    private String userName;

    private String password;

    private boolean readme = false;

    @NotBlank(message = "类型不能为空")
    private String ac;

}
