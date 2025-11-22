package com.spring.boot.disk.server.model.resp;

import com.spring.boot.disk.server.security.LoginInfo;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class UserInfoModel {

    private Long userId;

    private String username;

    private String name;

    private String email;

    private String avatar;

    public UserInfoModel(LoginInfo loginInfo) {
        this.userId = loginInfo.getUserId();
        this.username = loginInfo.getUserName();
        this.name = loginInfo.getUserNick();
        this.email = loginInfo.getEmail();
    }

}

