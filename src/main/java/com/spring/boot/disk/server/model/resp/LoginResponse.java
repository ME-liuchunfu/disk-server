package com.spring.boot.disk.server.model.resp;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LoginResponse {

    /** token 名称 */
    public String tokenName;

    /** token 值 */
    public String tokenValue;

    public String loginType;

    /** token 剩余有效期（单位: 秒） */
    public long tokenTimeout;

}
