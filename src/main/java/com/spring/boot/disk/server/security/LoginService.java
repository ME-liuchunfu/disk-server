package com.spring.boot.disk.server.security;

import cn.dev33.satoken.config.SaTokenConfig;
import cn.dev33.satoken.stp.SaTokenInfo;
import cn.dev33.satoken.stp.StpUtil;
import cn.dev33.satoken.stp.parameter.SaLoginParameter;
import cn.hutool.core.util.StrUtil;
import cn.hutool.crypto.digest.BCrypt;
import com.spring.boot.disk.server.conf.DiskServerConfig;
import com.spring.boot.disk.server.constant.LoginDriverType;
import com.spring.boot.disk.server.constant.LoginSession;
import com.spring.boot.disk.server.constant.UserStatus;
import com.spring.boot.disk.server.entity.po.VmDiskUser;
import com.spring.boot.disk.server.exception.AppException;
import com.spring.boot.disk.server.model.params.AuthModel;
import com.spring.boot.disk.server.model.params.RegisterModel;
import com.spring.boot.disk.server.model.resp.LoginResponse;
import com.spring.boot.disk.server.service.VmDiskUserService;
import jakarta.annotation.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import java.util.Date;
import java.util.Objects;

@Component
public class LoginService {

    @Resource
    VmDiskUserService vmDiskUserService;

    @Resource
    DiskServerConfig diskServerConfig;

    /**
     * 授权登录认证
     */
    public LoginResponse login(AuthModel authModel) {
        VmDiskUser vmDiskUser = null;
        if (StrUtil.equalsIgnoreCase("ac", authModel.getAc())) {
            vmDiskUser = vmDiskUserService.selectByUserName(authModel.getUserName());
        } else {
            throw new AppException("login type not support!!!");
        }
        if (Objects.isNull(vmDiskUser)) {
            throw new AppException("账号密码不正确", HttpStatus.FORBIDDEN.value());
        }
        if (!Objects.equals(vmDiskUser.getAcStatus(), UserStatus.OK.getStatus())) {
            throw new AppException("账号异常", HttpStatus.FORBIDDEN.value());
        }
//        String gensalt = BCrypt.gensalt(diskServerConfig.getPwdsalt());
//        String pw_hash = BCrypt.hashpw(authModel.getPassword(), gensalt);
        if (!BCrypt.checkpw(authModel.getPassword(), vmDiskUser.getPassword())) {
            throw new AppException("账号或密码不正确", HttpStatus.FORBIDDEN.value());
        }

        SaTokenConfig tokenConfig = StpUtil.getStpLogic().getConfigOrGlobal();
        SaLoginParameter saLoginParameter = new SaLoginParameter(tokenConfig);
        saLoginParameter.setDeviceType(LoginDriverType.PC.getDriver());
        saLoginParameter.setExtra(LoginSession.LOGIN_USER.getKey(), new LoginInfo(vmDiskUser));
        StpUtil.login(vmDiskUser.getUserId(), saLoginParameter);

        LoginResponse response = new LoginResponse();
        SaTokenInfo tokenInfo = StpUtil.getTokenInfo();
        response.setLoginType(tokenInfo.getLoginType());
        response.setTokenTimeout(tokenInfo.getTokenTimeout());
        response.setTokenName(tokenInfo.getTokenName());
        response.setTokenValue(tokenInfo.getTokenValue());
        return response;
    }

    public void register(RegisterModel registerModel) {
        VmDiskUser vmDiskUser = vmDiskUserService.selectByUserName(registerModel.getUserName());
        if (Objects.nonNull(vmDiskUser)) {
            throw new AppException("账号已存在");
        }
        String gensalt = BCrypt.gensalt(diskServerConfig.getPwdsalt());
        String pw_hash = BCrypt.hashpw(registerModel.getPassword(), gensalt);
        vmDiskUser = new VmDiskUser();
        vmDiskUser.setUserName(registerModel.getUserName());
        vmDiskUser.setUserNick(registerModel.getNickName());
        vmDiskUser.setPassword(pw_hash);
        vmDiskUser.setAcStatus(UserStatus.OK.getStatus());
        vmDiskUser.setEmail(registerModel.getEmail());
        vmDiskUser.setCreateTime(new Date());
        vmDiskUserService.save(vmDiskUser);
    }

}
