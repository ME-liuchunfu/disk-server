package com.spring.boot.disk.server.controller.api;

import com.spring.boot.disk.server.model.Rs;
import com.spring.boot.disk.server.model.params.AuthModel;
import com.spring.boot.disk.server.model.params.RegisterModel;
import com.spring.boot.disk.server.model.resp.LoginResponse;
import com.spring.boot.disk.server.security.LoginService;
import jakarta.annotation.Resource;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;


@RequestMapping("api/auth")
@RestController
public class ApiAuthController {

    @Resource
    LoginService loginService;

    @PostMapping("/login")
    public Rs login(@Validated @RequestBody AuthModel authModel) {
        LoginResponse response = loginService.login(authModel);
        return Rs.ok().data(response);
    }

    @PostMapping("/register")
    public Rs register(@Validated @RequestBody RegisterModel registerModel) {
        loginService.register(registerModel);
        return Rs.ok();
    }

    @RequestMapping("/logout")
    public Rs logout() {
        loginService.logout();
        return Rs.ok();
    }

}
