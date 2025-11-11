package com.spring.boot.disk.server.security;

import com.spring.boot.disk.server.entity.po.VmDiskUser;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@Getter
@Setter
@NoArgsConstructor
public class LoginInfo {

    private Long userId;

    private String userName;

    private String userNick;

    private String email;

    public LoginInfo(VmDiskUser vmDiskUser) {
        this.userId = vmDiskUser.getUserId();
        this.userName = vmDiskUser.getUserName();
        this.userNick = vmDiskUser.getUserNick();
        this.email = vmDiskUser.getEmail();
    }

}
