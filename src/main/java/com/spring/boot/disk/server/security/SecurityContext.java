package com.spring.boot.disk.server.security;

import cn.dev33.satoken.stp.StpUtil;
import cn.dev33.satoken.stp.parameter.SaLoginParameter;
import com.spring.boot.disk.server.constant.LoginSession;
import lombok.extern.slf4j.Slf4j;
import java.util.Objects;
import java.util.Optional;

@Slf4j
public class SecurityContext {

    /**
     * 获取用户id
     * @return
     */
    public static Long getUserId() {
        LoginInfo loginInfo = getLoginInfo();
        return Optional.ofNullable(loginInfo).map(LoginInfo::getUserId).orElse(null);
    }

    /**
     * 获取用户名
     * @return String or null
     */
    public static String getUserName() {
        LoginInfo loginInfo = getLoginInfo();
        return Optional.ofNullable(loginInfo).map(LoginInfo::getUserName).orElse(null);
    }

    /**
     * 获取登录用户信息
     * @return LoginInfo or null
     */
    public static LoginInfo getLoginInfo() {
        try {
            Object extra = StpUtil.getExtra(LoginSession.LOGIN_USER.getKey());
            if (Objects.nonNull(extra)) {
                if (extra instanceof LoginInfo) {
                    return (LoginInfo) extra;
                }
                if (extra instanceof cn.hutool.json.JSONObject) {
                    return ((cn.hutool.json.JSONObject)extra).toBean(LoginInfo.class);
                }
            }
        } catch (Exception e) {

        }
        return null;
    }

    public static void setLoginInfo(SaLoginParameter saLoginParameter, LoginInfo loginInfo) {
        saLoginParameter.setExtra(LoginSession.LOGIN_USER.getKey(), loginInfo);
    }

}
