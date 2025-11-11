package com.spring.boot.disk.server.controller.api;

import cn.dev33.satoken.stp.SaTokenInfo;
import cn.dev33.satoken.stp.StpUtil;
import com.spring.boot.disk.server.model.Rs;
import com.spring.boot.disk.server.model.params.AuthModel;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;


@RequestMapping("api/auth")
@RestController
public class ApiAuthController {

    @PostMapping("/login")
    public Rs login(@Validated @RequestBody AuthModel authModel) {
        StpUtil.login(10001);
        SaTokenInfo tokenInfo = StpUtil.getTokenInfo();
        return Rs.ok().data(tokenInfo);
    }

    @RequestMapping("/logout")
    public Rs logout() {
        StpUtil.logout();
        return Rs.ok();
    }

}
