package com.spring.boot.disk.server.model.params;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RegisterModel {

    @Size(min = 6, max = 12)
    @NotBlank(message = "账号不能为空")
    private String userName;

    @Size(min = 5, max = 12)
    @NotBlank(message = "昵称不能为空")
    private String nickName;

    @Size(min = 6, max = 12)
    @NotBlank(message = "密码不能为空")
    private String password;

    private String email;

    @NotBlank(message = "类型不能为空")
    private String ac = "ac";

}
